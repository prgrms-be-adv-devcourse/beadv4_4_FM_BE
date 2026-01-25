import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { marketApi, type ProductResponse } from '../api/market';
import { cartApi } from '../api/cart';
import { Button } from '../components/Button';
import { FaLeaf, FaShieldAlt, FaTruck } from 'react-icons/fa';

const ProductDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [product, setProduct] = useState<ProductResponse | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchProduct = async () => {
            if (!id) return;
            try {
                const data = await marketApi.getProduct(Number(id));
                if (data && (data.resultCode?.startsWith('2') || data.resultCode?.startsWith('S-2'))) {
                    setProduct(data.data as any);
                } else {
                    setError(data.msg || '상품을 찾을 수 없습니다.');
                }
            } catch (err) {
                console.error("Failed to fetch product", err);
                setError('상품 정보를 불러오는 중 오류가 발생했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchProduct();
    }, [id]);

    const [quantity, setQuantity] = useState(1);

    const handleAddToCart = async () => {
        if (!product) return;
        try {
            await cartApi.addToCart(product.productId, quantity);
            if (window.confirm('장바구니에 담겼습니다. 장바구니로 이동하시겠습니까?')) {
                navigate('/cart');
            }
        } catch (err: any) {
            console.error("Failed to add to cart", err);

            if (err.message === "로그인이 필요합니다." || err.response?.status === 401) {
                alert("로그인이 필요합니다.");
                navigate('/login');
            } else {
                alert(err.message || '장바구니 담기에 실패했습니다.');
            }
        }
    };

    const handleDirectOrder = async (type: 'TOSS' | 'CASH') => {
        if (!product) return;

        if (!window.confirm(type === 'TOSS' ? "바로 주문하시겠습니까?" : "예치금으로 바로 결제하시겠습니까?")) return;

        try {
            const orderItems = [{
                productId: product.productId,
                sellerId: (product as any).sellerId || 0,
                productName: product.name,
                categoryName: (product as any).categoryName || "",
                price: product.price,
                weight: product.weight || 0,
                thumbnailUrl: product.thumbnail || "",
                quantity: quantity
            }];

            const requestTotalPrice = product.price * quantity;

            const orderRequest = {
                totalPrice: requestTotalPrice,
                paymentType: type === 'TOSS' ? "CARD" : "CASH",
                items: orderItems
            };

            const orderResponse = await import('../api/order').then(({ orderApi }) => orderApi.createOrder(orderRequest));

            if (!orderResponse.data || !orderResponse.data.orderId) {
                throw new Error('주문 생성에 실패했습니다.');
            }

            const { orderNo } = orderResponse.data;
            const uniqueOrderId = `${orderNo}__${Date.now()}`;

            if (type === 'TOSS') {
                const { loadTossPayments } = await import('@tosspayments/payment-sdk');
                const { TOSS_CLIENT_KEY } = await import('../api/payment');
                const tossPayments = await loadTossPayments(TOSS_CLIENT_KEY);

                await tossPayments.requestPayment('카드', {
                    amount: requestTotalPrice,
                    orderId: uniqueOrderId,
                    orderName: `${product.name} ${quantity > 1 ? `외 ${quantity - 1}건` : ''}`,
                    successUrl: `${window.location.origin}/payment/success`,
                    failUrl: `${window.location.origin}/payment/fail`,
                });
            } else {
                window.location.href = `/payment/success?orderId=${uniqueOrderId}&amount=${requestTotalPrice}&method=CASH&status=DONE`;
            }

        } catch (err: any) {
            console.error("Order failed", err);
            alert("주문 접수 중 오류가 발생했습니다.\n" + (err.response?.data?.msg || err.message));
        }
    };

    if (loading) return (
        <div className="flex items-center justify-center min-h-[60vh] mt-20">
            <div className="text-lg text-[var(--text-muted)] animate-pulse">상품 상세정보를 불러오는 중...</div>
        </div>
    );

    if (error) return (
        <div className="flex items-center justify-center min-h-[60vh] mt-20">
            <div className="text-lg text-red-500">{error}</div>
        </div>
    );

    if (!product) return null;

    return (
        <div className="min-h-screen bg-[var(--background-color)] pt-32 pb-20">
            <div className="container mx-auto px-4 max-w-6xl">
                <button
                    onClick={() => navigate(-1)}
                    className="mb-8 text-[var(--text-muted)] hover:text-[var(--primary-color)] transition-colors flex items-center gap-2 font-medium"
                >
                    &larr; 돌아가기
                </button>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-12 lg:gap-20 bg-white p-8 md:p-12 rounded-[2rem] border border-[var(--border-color)] shadow-sm">
                    {/* Image Section */}
                    <div className="space-y-4">
                        <div className="aspect-square bg-[#f8fafc] rounded-[1.5rem] overflow-hidden flex items-center justify-center relative shadow-inner">
                            {product.thumbnail ? (
                                <img
                                    src={product.thumbnail}
                                    alt={product.name}
                                    className="w-full h-full object-cover hover:scale-105 transition-transform duration-700"
                                />
                            ) : (
                                <div className="text-center opacity-30">
                                    <FaLeaf className="text-8xl mx-auto mb-4 text-[var(--secondary-color)]" />
                                </div>
                            )}
                        </div>
                        {/* More images could go here as thumbnails */}
                        <div className="grid grid-cols-3 gap-4">
                            <div className="text-center p-4 bg-green-50 rounded-xl">
                                <FaLeaf className="mx-auto text-green-600 mb-2" />
                                <span className="text-xs font-semibold text-green-700">친환경 소재</span>
                            </div>
                            <div className="text-center p-4 bg-blue-50 rounded-xl">
                                <FaShieldAlt className="mx-auto text-blue-600 mb-2" />
                                <span className="text-xs font-semibold text-blue-700">인증 완료</span>
                            </div>
                            <div className="text-center p-4 bg-orange-50 rounded-xl">
                                <FaTruck className="mx-auto text-orange-600 mb-2" />
                                <span className="text-xs font-semibold text-orange-700">안전 배송</span>
                            </div>
                        </div>
                    </div>

                    {/* Info Section */}
                    <div className="flex flex-col">
                        <div className="mb-2">
                            <span className={`inline-block px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider mb-4 ${product.status === 'FOR_SALE' ? 'bg-blue-100 text-blue-700' : 'bg-red-100 text-red-700'}`}>
                                {product.status === 'FOR_SALE' ? 'In Stock' : 'Sold Out'}
                            </span>
                        </div>

                        <h1 className="text-4xl md:text-5xl font-serif font-bold text-[var(--text-main)] mb-4 leading-tight">
                            {product.name}
                        </h1>

                        <p className="text-3xl font-bold text-[var(--primary-color)] mb-8 border-b border-[var(--border-color)] pb-8">
                            {product.price?.toLocaleString()} <span className="text-lg font-normal text-[var(--text-muted)]">원</span>
                        </p>

                        <div className="prose prose-lg text-[var(--text-muted)] mb-10 leading-relaxed max-w-none">
                            <p>{product.description}</p>
                        </div>

                        <div className="grid grid-cols-2 gap-8 mb-10 text-sm">
                            <div>
                                <span className="block text-[var(--text-muted)] mb-1">잔여 수량</span>
                                <span className="block font-semibold text-[var(--text-main)] text-lg">{product.quantity}개</span>
                            </div>
                            <div>
                                <span className="block text-[var(--text-muted)] mb-1">무게</span>
                                <span className="block font-semibold text-[var(--text-main)] text-lg">{product.weight ?? 0}g</span>
                            </div>
                        </div>

                        <div className="mt-auto space-y-8">
                            {/* Quantity Selector */}
                            <div>
                                <label className="block text-sm font-bold text-[var(--text-main)] mb-3">수량 선택</label>
                                <div className="flex items-center gap-4 bg-slate-50 inline-flex p-2 rounded-full border border-slate-200">
                                    <button
                                        onClick={() => setQuantity(Math.max(1, quantity - 1))}
                                        className="w-10 h-10 rounded-full bg-white shadow-sm flex items-center justify-center text-lg font-bold hover:bg-gray-100 transition-colors"
                                    >-</button>
                                    <input
                                        type="number"
                                        value={quantity}
                                        onChange={(e) => setQuantity(Math.max(1, Math.min(product.quantity || 99, Number(e.target.value))))}
                                        className="w-16 text-center bg-transparent font-bold text-lg outline-none"
                                    />
                                    <button
                                        onClick={() => setQuantity(Math.min(product.quantity || 99, quantity + 1))}
                                        className="w-10 h-10 rounded-full bg-white shadow-sm flex items-center justify-center text-lg font-bold hover:bg-gray-100 transition-colors"
                                    >+</button>
                                </div>
                            </div>

                            <div className="flex flex-col gap-4">
                                <div className="flex gap-4">
                                    <Button
                                        variant="outline"
                                        size="lg"
                                        fullWidth
                                        onClick={handleAddToCart}
                                        className="h-14"
                                    >
                                        장바구니 담기
                                    </Button>
                                    <Button
                                        variant="primary"
                                        size="lg"
                                        fullWidth
                                        onClick={() => handleDirectOrder('TOSS')}
                                        className="h-14 bg-primary-color"
                                    >
                                        카드 결제하기
                                    </Button>
                                    <Button

                                        size="lg"
                                        fullWidth
                                        onClick={() => handleDirectOrder('CASH')}
                                        className="h-14 bg-emerald-600 text-white hover:bg-emerald-700 border-none shadow-lg shadow-emerald-900/10"
                                    >
                                        예치금 결제
                                    </Button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProductDetailPage;
