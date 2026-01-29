import { useEffect, useState, useRef } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { memberApi } from '../api/member';
import { walletApi, type UserWalletResponseDto } from '../api/wallet';
import { FaBox, FaExchangeAlt, FaStore } from 'react-icons/fa';

const MyPage = () => {
    const navigate = useNavigate();
    const [walletInfo, setWalletInfo] = useState<UserWalletResponseDto | null>(null);
    const [loading, setLoading] = useState(true);

    const alertShown = useRef(false);

    useEffect(() => {
        const fetchInfo = async () => {
            const token = localStorage.getItem('accessToken');
            if (!token) {
                if (!alertShown.current) {
                    alert('로그인이 필요합니다.');
                    alertShown.current = true;
                    navigate('/login');
                }
                return;
            }

            try {
                // 1. Get User ID and seller status from /me
                const meResponse = await memberApi.getMe();
                if (meResponse.resultCode.startsWith('S-200') || meResponse.resultCode.startsWith('200')) {
                    const meData = meResponse.data;
                    let userInfoFromMe: any = null;
                    console.log('DEBUG ME DATA:', meData); // Debug log

                    // Check if response is Object (New) or Number (Legacy)
                    if (typeof meData === 'object' && meData !== null) {
                        userInfoFromMe = meData;
                    } else if (typeof meData === 'number') {
                        // Handle legacy response where data is just userId
                        userInfoFromMe = { userId: meData };
                    }

                    // Prepare default/fallback wallet data
                    let walletData = {
                        walletId: 0,
                        balance: 0,
                        user: {
                            id: (userInfoFromMe as any).userId || (userInfoFromMe as any).id || 0,
                            email: (userInfoFromMe as any).email || '정보 없음',
                            name: (userInfoFromMe as any).name || (userInfoFromMe as any).username || '사용자',
                            nickname: (userInfoFromMe as any).nickname || localStorage.getItem('nickname') || 'User',
                            profileImage: (userInfoFromMe as any).profileImage || null,
                            createdAt: (userInfoFromMe as any).createdAt || new Date().toISOString(),
                            sellerStatus: (userInfoFromMe as any).status
                        }
                    };

                    try {
                        try {
                            // 2. Try to get User Wallet Balance (safer API)
                            // Using getBalance() to avoid potential serialization errors with getUserWallet()
                            const balanceResponse = await walletApi.getBalance();
                            if (balanceResponse.resultCode.startsWith('S-200') || balanceResponse.resultCode.startsWith('200')) {
                                walletData.balance = balanceResponse.data;
                                // walletId is not returned by getBalance, but it's okay, defaulting to 0
                            }
                        } catch (walletError) {
                            console.warn('Wallet balance fetch failed (using default 0):', walletError);
                        }
                    } catch (walletError) {
                        console.warn('Wallet info fetch failed (using default):', walletError);
                    }

                    // Set state with safe data
                    setWalletInfo(walletData as any);
                }
            } catch (e) {
                console.error('Failed to fetch me info', e);
            } finally {
                setLoading(false);
            }
        };

        fetchInfo();
    }, [navigate]);

    if (loading) return <div className="container" style={{ textAlign: 'center', marginTop: '6rem' }}>로딩 중...</div>;

    if (!walletInfo) return (
        <div className="container" style={{ textAlign: 'center', marginTop: '6rem' }}>
            <p>정보를 불러올 수 없습니다. 다시 로그인해주세요.</p>
            <button onClick={() => navigate('/login')} className="btn btn-primary">로그인 페이지로</button>
        </div>
    );

    const { user, balance } = walletInfo;

    return (
        <div className="container" style={{ maxWidth: '900px', margin: '2rem auto', marginTop: '120px' }}>
            <h1 style={{ marginBottom: '2rem', fontSize: '2rem', textAlign: 'left', fontWeight: 700, letterSpacing: '-0.02em', color: 'var(--primary-color)' }}>마이 페이지</h1>

            {/* 1. Profile Section - Horizontal Layout */}
            <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', marginBottom: '1.5rem', padding: '1.75rem' }}>
                <div style={{
                    minWidth: '80px', height: '80px', borderRadius: '50%', backgroundColor: '#f1f5f9',
                    display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '2.5rem', overflow: 'hidden',
                    boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.05)'
                }}>
                    {user.profileImage && user.profileImage !== 'default.png' ? (
                        <img src={user.profileImage} alt="Profile" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                    ) : (
                        <span style={{ fontSize: '2rem' }}>👤</span>
                    )}
                </div>
                <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.25rem' }}>
                        <h2 style={{ fontSize: '1.4rem', margin: 0, fontWeight: 700 }}>{user.nickname}</h2>
                        <span style={{ fontSize: '0.75rem', padding: '0.15rem 0.5rem', borderRadius: '20px', backgroundColor: 'var(--primary-color)', color: 'white', fontWeight: 500 }}>Member</span>
                    </div>
                    <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>{user.email}</p>
                    {/* Removed name display if it's redundant or user prefers nickname focus */}
                </div>
                <Link to="/profile/edit" className="btn btn-outline" style={{ fontSize: '0.9rem', padding: '0.5rem 1.2rem', borderRadius: '50px' }}>
                    프로필 편집
                </Link>
            </div>

            {/* 2. Dashboard Status Grid - 3 Columns */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '1rem', marginBottom: '1.5rem' }}>
                {/* Wallet Info */}
                <div className="card" style={{ padding: '2rem', display: 'flex', flexDirection: 'column', justifyContent: 'center', backgroundColor: '#fafaf9' }}>
                    <h3 style={{ fontSize: '1rem', marginBottom: '0.75rem', color: 'var(--text-muted)', fontWeight: 500 }}>적립된 기부금</h3>
                    <div style={{ fontSize: '1.5rem', fontWeight: '800', color: 'var(--primary-color)', marginBottom: '1rem' }}>
                        {balance.toLocaleString()}<span style={{ fontSize: '1rem', fontWeight: 400, marginLeft: '0.2rem' }}>원</span>
                    </div>
                    <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button className="btn btn-primary" onClick={() => alert('기부금 증명서 발급 기능은 준비 중입니다.')} style={{ flex: 1, padding: '0.6rem', fontSize: '0.85rem', borderRadius: '50px' }}>증명서</button>
                        <Link to="/wallet/history" className="btn btn-outline" style={{ flex: 1, padding: '0.6rem', fontSize: '0.85rem', borderRadius: '50px', backgroundColor: 'white' }}>내역</Link>
                    </div>
                </div>

                {/* Order Status Summary */}
                <div className="card" style={{ gridColumn: 'span 2', padding: '1.5rem', display: 'flex', flexDirection: 'column' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                        <h3 style={{ fontSize: '1.1rem', margin: 0, fontWeight: 600 }}>주문 현황 <span style={{ fontSize: '0.9rem', color: '#94a3b8', fontWeight: 400, marginLeft: '0.5rem' }}>(최근 3개월)</span></h3>
                        <Link to="/orders" style={{ fontSize: '0.9rem', color: 'var(--text-muted)', textDecoration: 'none' }}>전체보기 ›</Link>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flex: 1, padding: '0 0.5rem' }}>
                        {[
                            { label: '주문접수', count: 0 },
                            { label: '결제완료', count: 0 },
                            { label: '배송준비', count: 0 },
                            { label: '배송중', count: 0 },
                            { label: '배송완료', count: 0 }
                        ].map((status, idx) => (
                            <div key={idx} style={{ textAlign: 'center', position: 'relative' }}>
                                <div style={{ fontSize: '1.25rem', fontWeight: '700', marginBottom: '0.25rem', color: status.count > 0 ? 'var(--primary-color)' : '#cbd5e1' }}>
                                    {status.count}
                                </div>
                                <div style={{ fontSize: '0.8rem', color: '#64748b' }}>{status.label}</div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* 3. Menu List - Grid Layout for cleaner look */}
            <h3 style={{ fontSize: '1.25rem', marginBottom: '1.5rem', fontWeight: 700 }}>쇼핑 이용정보</h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <Link to="/orders" className="card" style={{ padding: '1.25rem', display: 'flex', alignItems: 'center', gap: '1rem', cursor: 'pointer', textDecoration: 'none', color: 'inherit', transition: 'border-color 0.2s' }}>
                    <div style={{ padding: '0.6rem', backgroundColor: '#f0fdf4', borderRadius: '50%', color: 'var(--primary-color)' }}>
                        <FaBox size={18} />
                    </div>
                    <div>
                        <div style={{ fontWeight: 600, fontSize: '1rem' }}>주문 목록</div>
                        <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '0.1rem' }}>주문하신 상품 내역을 확인하세요</div>
                    </div>
                </Link>

                <Link to="/refunds" className="card" style={{ padding: '1.25rem', display: 'flex', alignItems: 'center', gap: '1rem', cursor: 'pointer', textDecoration: 'none', color: 'inherit', transition: 'border-color 0.2s' }}>
                    <div style={{ padding: '0.6rem', backgroundColor: '#fff7ed', borderRadius: '50%', color: '#ea580c' }}>
                        <FaExchangeAlt size={18} />
                    </div>
                    <div>
                        <div style={{ fontWeight: 600, fontSize: '1rem' }}>취소 / 반품 / 교환</div>
                        <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '0.1rem' }}>취소 및 반품 내역을 확인하세요</div>
                    </div>
                </Link>

                <div className="card" style={{ gridColumn: 'span 2', marginTop: '0.5rem', padding: '1.25rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', backgroundColor: '#f8fafc', border: '1px dashed #cbd5e1' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                        <div style={{ padding: '0.6rem', backgroundColor: '#fff', borderRadius: '50%', color: 'var(--primary-color)', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
                            <FaStore size={22} />
                        </div>
                        <div>
                            <div style={{ fontWeight: 700, fontSize: '1rem', color: '#1e293b' }}>
                                {(user as any).sellerStatus === 'APPROVED' ? '나의 상점 관리' : '판매자이신가요?'}
                            </div>
                            <div style={{ fontSize: '0.85rem', color: '#64748b', marginTop: '0.1rem' }}>
                                {(user as any).sellerStatus === 'APPROVED' ? '상품 등록 및 주문 관리를 시작해보세요!' : '판매자로 등록하고 나만의 상점을 오픈해보세요!'}
                            </div>
                        </div>
                    </div>
                    {(user as any).sellerStatus === 'PENDING' ? (
                        <button className="btn" disabled style={{ padding: '0.75rem 1.5rem', borderRadius: '50px', fontSize: '0.95rem', backgroundColor: '#e2e8f0', color: '#94a3b8', cursor: 'not-allowed', border: 'none' }}>
                            승인 대기중
                        </button>
                    ) : (user as any).sellerStatus === 'APPROVED' ? (
                        <Link to="/myshop" className="btn btn-primary" style={{ padding: '0.75rem 1.5rem', borderRadius: '50px', fontSize: '0.95rem', backgroundColor: '#0f766e', border: 'none' }}>
                            내 상점 가기
                        </Link>
                    ) : (
                        <Link to="/seller-request" className="btn btn-primary" style={{ padding: '0.75rem 1.5rem', borderRadius: '50px', fontSize: '0.95rem' }}>
                            판매자 신청하기
                        </Link>
                    )}
                </div>
            </div>

            <div style={{ marginTop: '3rem', textAlign: 'center', borderTop: '1px solid #e2e8f0', paddingTop: '1.5rem' }}>
                <p style={{ fontSize: '0.9rem', color: '#94a3b8' }}>Mossy 고객센터 1588-0000 (평일 09:00~18:00)</p>
                <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', marginTop: '0.5rem', fontSize: '0.85rem', color: '#cbd5e1' }}>
                    <span>이용약관</span>
                    <span>|</span>
                    <span>개인정보처리방침</span>
                </div>
            </div>
        </div>
    );
};

export default MyPage;
