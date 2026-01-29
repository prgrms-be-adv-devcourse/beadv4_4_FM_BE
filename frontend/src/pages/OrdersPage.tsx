import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { orderApi, type OrderResponse, type OrderDetailResponse } from '../api/order';
import { paymentApi, type PaymentResponse } from '../api/payment';

const OrdersPage = () => {
    const navigate = useNavigate();
    const [orders, setOrders] = useState<OrderResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Expanded State & Data Cache
    const [expandedOrderId, setExpandedOrderId] = useState<number | null>(null);
    const [detailsCache, setDetailsCache] = useState<Record<number, { items: OrderDetailResponse[], payments: PaymentResponse[] }>>({});
    const [loadingDetails, setLoadingDetails] = useState(false);

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                // Backend returns a Page object directly
                const pageData = await orderApi.getMyOrders();

                // Spring Data Page object structure: { content: [...], totalElements: ..., ... }
                if (pageData && Array.isArray(pageData.content)) {
                    setOrders(pageData.content);
                } else {
                    // Fallback or empty
                    setOrders([]);
                }
            } catch (err: any) {
                console.error('Failed to fetch orders:', err);
                setError('주문 내역을 불러오는 중 오류가 발생했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchOrders();
    }, []);

    const toggleOrder = async (orderId: number, orderNo: string) => {
        if (expandedOrderId === orderId) {
            setExpandedOrderId(null); // Close
            return;
        }

        setExpandedOrderId(orderId); // Open

        // If data is already cached, don't fetch again
        if (detailsCache[orderId]) return;

        setLoadingDetails(true);
        try {
            // Fetch Order Details
            const items = await orderApi.getOrderDetails(orderId);

            // Fetch Payments (using original orderNo without timestamp suffix)
            const originalOrderNo = orderNo.split('__')[0];
            let payments: PaymentResponse[] = [];

            try {
                const paymentsRs = await paymentApi.getPaymentsByOrder(originalOrderNo);
                payments = Array.isArray(paymentsRs) ? paymentsRs : (paymentsRs as any).data || [];
            } catch (paymentErr) {
                console.warn('Payment info load failed:', paymentErr);
                // Payments might be empty if failed, but we still show items
            }

            setDetailsCache(prev => ({
                ...prev,
                [orderId]: { items: Array.isArray(items) ? items : [], payments }
            }));
        } catch (err) {
            console.error('Failed to fetch order details:', err);
            // Set empty state to show "No info" message instead of alert
            setDetailsCache(prev => ({
                ...prev,
                [orderId]: { items: [], payments: [] }
            }));
        } finally {
            setLoadingDetails(false);
        }
    };

    if (loading) return <div className="container" style={{ textAlign: 'center', marginTop: '6rem' }}>로딩 중...</div>;

    if (error) {
        return (
            <div className="container" style={{ textAlign: 'center', marginTop: '6rem' }}>
                <p style={{ color: 'var(--danger-color)' }}>{error}</p>
                <button onClick={() => navigate('/mypage')} className="btn btn-primary">돌아가기</button>
            </div>
        );
    }

    return (
        <div className="container" style={{ maxWidth: '800px', margin: '2rem auto', marginTop: '120px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                <h1 style={{ fontSize: '2rem', fontWeight: 700, color: 'var(--primary-color)' }}>주문 내역</h1>
                <button onClick={() => navigate('/mypage')} className="btn btn-outline">마이페이지</button>
            </div>

            {orders.length === 0 ? (
                <div className="card" style={{ padding: '4rem 2rem', textAlign: 'center' }}>
                    <p>주문 내역이 없습니다.</p>
                </div>
            ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    {orders.map((order) => {
                        const isExpanded = expandedOrderId === order.orderId;
                        const details = detailsCache[order.orderId];

                        return (
                            <div key={order.orderId} className="card" style={{ padding: '0', overflow: 'hidden', border: isExpanded ? '1px solid var(--primary-color)' : '1px solid #e2e8f0', transition: 'all 0.2s' }}>
                                {/* Order Summary (Clickable) */}
                                <div
                                    onClick={() => toggleOrder(order.orderId, order.orderNo)}
                                    style={{ padding: '1.5rem', cursor: 'pointer', backgroundColor: isExpanded ? '#f8fafc' : 'white' }}
                                >
                                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem' }}>
                                        <div>
                                            <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '0.25rem' }}>
                                                {new Date(order.createdAt).toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' })}
                                            </div>
                                            <div style={{ fontSize: '1.1rem', fontWeight: 600 }}>주문번호: {order.orderNo}</div>
                                        </div>
                                        <div style={{
                                            padding: '0.4rem 1rem', borderRadius: '20px', fontSize: '0.85rem', fontWeight: 600,
                                            backgroundColor: getStatusColor(order.state).bg, color: getStatusColor(order.state).text,
                                            height: 'fit-content'
                                        }}>
                                            {getStatusText(order.state)}
                                        </div>
                                    </div>
                                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                        <div>
                                            <div style={{ fontWeight: 600 }}>총 {order.itemCount}개 상품</div>
                                            <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>{order.address || '배송지 정보 없음'}</div>
                                        </div>
                                        <div style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--primary-color)' }}>
                                            {order.totalPrice.toLocaleString()}원
                                            <span style={{ fontSize: '0.8rem', marginLeft: '0.5rem', color: '#94a3b8' }}>{isExpanded ? '▲' : '▼'}</span>
                                        </div>
                                    </div>
                                </div>

                                {/* Expanded Details */}
                                {isExpanded && (
                                    <div style={{ borderTop: '1px solid #e2e8f0', padding: '1.5rem', backgroundColor: '#fff' }}>
                                        {loadingDetails && !details ? (
                                            <div style={{ textAlign: 'center', padding: '1rem' }}>불러오는 중...</div>
                                        ) : details ? (
                                            <>
                                                {details.items && details.items.length > 0 ? (
                                                    <>
                                                        <h4 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '1rem' }}>주문 상품</h4>
                                                        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginBottom: '2rem' }}>
                                                            {details.items.map((item, idx) => (
                                                                <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', padding: '0.75rem', backgroundColor: '#f8fafc', borderRadius: '8px' }}>
                                                                    <div>
                                                                        <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{item.sellerName || '판매자 정보 없음'}</div>
                                                                        <div style={{ fontWeight: 600 }}>상품 ID: {item.productId}</div>
                                                                        <div style={{ fontSize: '0.9rem' }}>수량: {item.quantity || 0}개</div>
                                                                    </div>
                                                                    <div style={{ fontWeight: 600, alignSelf: 'center' }}>{(item.orderPrice || 0).toLocaleString()}원</div>
                                                                </div>
                                                            ))}
                                                        </div>

                                                        <h4 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '1rem' }}>결제 내역</h4>
                                                        {details.payments && details.payments.length > 0 ? (
                                                            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                                                                {details.payments.map((payment, idx) => (
                                                                    <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.9rem', padding: '0.5rem 0', borderBottom: '1px dashed #e2e8f0' }}>
                                                                        <div>
                                                                            <span style={{ fontWeight: 600, marginRight: '0.5rem' }}>{payment.payMethod || '결제수단'}</span>
                                                                            <span style={{ color: '#64748b' }}>({payment.status || '상태없음'})</span>
                                                                        </div>
                                                                        <div>{(payment.amount || 0).toLocaleString()}원</div>
                                                                    </div>
                                                                ))}
                                                            </div>
                                                        ) : (
                                                            <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>결제 내역이 없습니다.</div>
                                                        )}
                                                    </>
                                                ) : (
                                                    <div style={{ textAlign: 'center', padding: '2rem 0', color: 'var(--text-muted)' }}>
                                                        <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>⚠️</div>
                                                        <p>주문 상세 정보를 불러올 수 없습니다.</p>
                                                    </div>
                                                )}
                                            </>
                                        ) : null}

                                    </div>
                                )}
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
};

// Helper functions (updated for 'state' field)
const getStatusText = (status: string): string => {
    const statusMap: Record<string, string> = {
        'PENDING': '주문접수',
        'PAID': '결제완료',
        'PREPARING': '배송준비',
        'SHIPPING': '배송중',
        'DELIVERED': '배송완료',
        'CANCELLED': '주문취소'
        // Add other states if needed
    };
    return statusMap[status] || status;
};

const getStatusColor = (status: string): { bg: string; text: string } => {
    const colorMap: Record<string, { bg: string; text: string }> = {
        'PENDING': { bg: '#fef3c7', text: '#92400e' },
        'PAID': { bg: '#dbeafe', text: '#1e40af' },
        'PREPARING': { bg: '#e0e7ff', text: '#4338ca' },
        'SHIPPING': { bg: '#ddd6fe', text: '#6b21a8' },
        'DELIVERED': { bg: '#d1fae5', text: '#065f46' },
        'CANCELLED': { bg: '#fee2e2', text: '#991b1b' }
    };
    return colorMap[status] || { bg: '#f1f5f9', text: '#475569' };
};

export default OrdersPage;
