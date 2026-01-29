import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FaSearch, FaLeaf, FaRecycle, FaTree, FaWineBottle, FaShoppingBag, FaArrowRight } from 'react-icons/fa';
import { marketApi, type ProductResponse } from '../api/market';
import { cartApi } from '../api/cart';
import { ProductCard } from '../components/ProductCard';
import { Button } from '../components/Button';

const HomePage = () => {
    const [searchTerm, setSearchTerm] = useState('');
    const [products, setProducts] = useState<ProductResponse[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const data = await marketApi.getProducts(0, 8); // Fetch top 8 items
                setProducts(data.data.content);
            } catch (error) {
                console.error("Failed to fetch home products", error);
            } finally {
                setLoading(false);
            }
        };
        fetchProducts();
    }, []);

    const categories = [
        { name: 'Living', label: '친환경 생활용품', icon: <FaLeaf className="w-5 h-5" /> },
        { name: 'Zero Waste', label: '제로웨이스트', icon: <FaRecycle className="w-5 h-5" /> },
        { name: 'Local Food', label: '로컬푸드', icon: <FaTree className="w-5 h-5" /> },
        { name: 'Upcycling', label: '업사이클링', icon: <FaWineBottle className="w-5 h-5" /> },
        { name: 'Refill', label: '리필용품', icon: <FaShoppingBag className="w-5 h-5" /> }
    ];

    const [searchResultProducts, setSearchResultProducts] = useState<ProductResponse[]>([]);

    // Live API Search with Debounce
    useEffect(() => {
        const delayDebounceFn = setTimeout(async () => {
            if (searchTerm.trim()) {
                try {
                    const data = await marketApi.getProducts(0, 20, searchTerm);
                    setSearchResultProducts(data.data.content);
                } catch (error) {
                    console.error("Search failed", error);
                }
            } else {
                setSearchResultProducts([]);
            }
        }, 500);

        return () => clearTimeout(delayDebounceFn);
    }, [searchTerm]);

    const addToCart = async (e: React.MouseEvent, productId: number) => {
        e.preventDefault(); // Prevent navigation
        try {
            await cartApi.addToCart(productId, 1);
            // Optional: Toast notification here
            if (window.confirm('장바구니에 담겼습니다. 장바구니로 이동하시겠습니까?')) {
                // Use window.location as quick nav since hook might need wrapping
                // or just ignore if user says canceled.
                window.location.href = '/cart';
            }
        } catch (err: any) {
            console.error(err);
            if (err.message === "로그인이 필요합니다." || err.response?.status === 401) {
                alert("로그인이 필요합니다.");
                window.location.href = '/login';
            } else {
                alert('장바구니 담기 실패: ' + (err.message || '오류가 발생했습니다.'));
            }
        }
    };

    return (
        <div className="pb-0 bg-[var(--background-color)]">
            {/* 1. Hero Banner */}
            <div className="relative min-h-[70vh] w-full flex flex-col justify-center items-center text-white text-center mb-20 bg-cover bg-center"
                style={{ backgroundImage: 'url(https://images.unsplash.com/photo-1470058869958-2a77ade41c02?q=80&w=2070&auto=format&fit=crop)' }}>

                {/* Overlay with gradient for better readability */}
                <div className="absolute inset-0 bg-gradient-to-b from-black/60 via-black/30 to-[var(--background-color)]"></div>

                <div className="relative z-10 w-full max-w-5xl px-6 transform translate-y-[-10%]">
                    <span className="inline-block py-1 px-3 border border-white/30 rounded-full text-sm font-medium tracking-widest mb-6 backdrop-blur-sm">PREMIUM ECO LIFESTYLE</span>
                    <h1 className="text-7xl md:text-9xl font-serif mb-6 text-white drop-shadow-2xl font-bold tracking-tight">Mossy</h1>
                    <p className="text-2xl md:text-3xl font-light mb-16 opacity-90 max-w-2xl mx-auto leading-relaxed">자연을 닮은 현명한 소비,<br />지속 가능한 일상을 시작하세요.</p>

                    <div className="relative max-w-2xl mx-auto group">
                        <div className="flex items-center bg-white/10 backdrop-blur-md rounded-full border border-white/20 p-2 shadow-2xl transition-all duration-300 focus-within:bg-white/20 focus-within:scale-105">
                            <div className="pl-6 text-white/70">
                                <FaSearch className="text-xl" />
                            </div>
                            <input
                                type="text"
                                placeholder="어떤 친환경 제품을 찾으시나요?"
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="w-full bg-transparent border-none outline-none text-white px-4 py-4 text-lg placeholder:text-white/60 font-medium"
                            />
                            <button className="bg-[var(--primary-color)] text-white px-8 py-3 rounded-full font-medium hover:bg-[var(--primary-hover)] transition-colors">
                                검색
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div className="container mx-auto px-4 max-w-[1200px]">

                {/* Search Results (Conditional) */}
                {searchTerm ? (
                    <div className="min-h-[500px]">
                        <h2 className="text-3xl font-bold mb-10 text-[var(--text-main)] border-b border-[var(--border-color)] pb-4">
                            <span className="text-[var(--primary-color)]">'{searchTerm}'</span> 검색 결과
                        </h2>
                        {searchResultProducts.length > 0 ? (
                            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
                                {searchResultProducts.map(product => (
                                    <ProductCard key={product.productId} product={product} onAddToCart={addToCart} />
                                ))}
                            </div>
                        ) : (
                            <div className="text-center py-32 text-[var(--text-muted)] text-xl bg-[var(--surface-color)] rounded-[var(--radius-lg)] border border-[var(--border-color)]">
                                검색 결과가 없습니다.
                            </div>
                        )}
                    </div>
                ) : (
                    <>
                        {/* 2. Categories */}
                        <div className="mb-28">
                            <div className="text-center mb-12">
                                <span className="text-[var(--accent-color)] font-bold tracking-wider uppercase text-sm mb-2 block">Categories</span>
                                <h2 className="text-4xl font-serif font-bold text-[var(--text-main)]">Curated Collections</h2>
                            </div>

                            <div className="flex flex-wrap justify-center gap-6">
                                {categories.map((cat, idx) => (
                                    <div key={idx}
                                        className="group flex flex-col items-center gap-4 w-40 p-6 rounded-[2rem] bg-white border border-[var(--border-color)] cursor-pointer hover:border-[var(--primary-color)] hover:shadow-lg hover:-translate-y-2 transition-all duration-300"
                                    >
                                        <div className="w-16 h-16 rounded-full bg-[#f1f5f9] flex items-center justify-center text-[var(--text-muted)] group-hover:bg-[var(--primary-color)] group-hover:text-white transition-colors duration-300">
                                            {cat.icon}
                                        </div>
                                        <div className="text-center">
                                            <span className="block font-bold text-[var(--text-main)] mb-1">{cat.label}</span>
                                            <span className="block text-xs text-[var(--text-muted)] font-medium uppercase tracking-wide">{cat.name}</span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* 3. Recommended Products */}
                        <div className="mb-32">
                            <div className="flex justify-between items-end mb-10 border-b border-[var(--border-color)] pb-4">
                                <div>
                                    <span className="text-[var(--accent-color)] font-bold tracking-wider uppercase text-xs mb-2 block">Selection</span>
                                    <h2 className="text-4xl font-serif font-bold text-[var(--primary-color)]">New Arrivals</h2>
                                </div>
                                <Link to="/market" className="group flex items-center text-sm text-[var(--text-muted)] hover:text-[var(--primary-color)] font-medium transition-colors">
                                    전체보기
                                    <span className="ml-2 w-8 h-8 rounded-full bg-white border border-[var(--border-color)] flex items-center justify-center group-hover:border-[var(--primary-color)] group-hover:bg-[var(--primary-color)] group-hover:text-white transition-all">
                                        <FaArrowRight className="text-xs" />
                                    </span>
                                </Link>
                            </div>

                            {loading ? (
                                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
                                    {[...Array(4)].map((_, i) => (
                                        <div key={i} className="aspect-[3/4] bg-slate-200 rounded-[var(--radius-lg)] animate-pulse"></div>
                                    ))}
                                </div>
                            ) : (
                                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
                                    {products.slice(0, 8).map(product => (
                                        <ProductCard key={product.productId} product={product} onAddToCart={addToCart} />
                                    ))}
                                </div>
                            )}
                        </div>

                        {/* 4. Brand Highlight */}
                        <div className="mb-32 p-12 lg:p-24 rounded-[3rem] bg-[#e6f4ea] flex flex-col md:flex-row items-center justify-between gap-16 relative overflow-visible">
                            <div className="max-w-xl relative z-10 order-2 md:order-1">
                                <span className="inline-block px-4 py-1.5 bg-[var(--primary-color)] text-white rounded-full text-xs font-bold uppercase tracking-wider mb-8 shadow-lg">Story</span>
                                <h2 className="text-5xl lg:text-6xl font-serif font-bold mb-8 text-slate-900 leading-tight">GreenMate<br /><span className="text-[var(--success-color)] text-4xl lg:text-5xl opacity-80">With Nature</span></h2>
                                <p className="text-xl text-slate-700 mb-10 leading-relaxed font-light">
                                    지속 가능한 내일을 위해 노력하는 그린메이트의 이야기를 만나보세요.
                                    작은 실천이 모여 큰 숲을 이룹니다.
                                </p>
                                <Button size="lg">브랜드 스토리 보기</Button>
                            </div>

                            <div className="order-1 md:order-2 w-full max-w-lg relative block">
                                <div className="absolute inset-0 bg-[var(--primary-color)] rounded-[2rem] transform rotate-6 scale-95 opacity-20 blur-xl"></div>
                                <div className="relative aspect-[4/5] rounded-[2rem] overflow-hidden shadow-2xl transform rotate-3 hover:rotate-0 transition-transform duration-700">
                                    <img src="https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?q=80&w=1913&auto=format&fit=crop" alt="Green Brand" className="w-full h-full object-cover" />
                                </div>
                            </div>
                        </div>

                        {/* 5. Popular Search Terms */}
                        <div className="mb-32 text-center">
                            <h3 className="text-sm font-bold text-[var(--text-muted)] uppercase tracking-widest mb-8">Trending Keywords</h3>
                            <div className="flex flex-wrap justify-center gap-4">
                                {['#텀블러', '#친환경주방', '#업사이클', '#대나무칫솔', '#플라스틱프리'].map(tag => (
                                    <span key={tag}
                                        className="px-6 py-3 rounded-full bg-white border border-[var(--border-color)] text-[var(--text-muted)] cursor-pointer transition-all hover:border-[var(--primary-color)] hover:text-[var(--primary-color)] hover:shadow-md text-sm font-medium"
                                        onClick={() => setSearchTerm(tag.replace('#', ''))}
                                    >
                                        {tag}
                                    </span>
                                ))}
                            </div>
                        </div>
                    </>
                )}
            </div>

            {/* 6. Footer */}
            <footer className="bg-[#1c2e26] text-white/80 py-20 mt-auto border-t border-white/10">
                <div className="container mx-auto px-4 max-w-[1200px] flex flex-col md:flex-row justify-between items-start gap-12">
                    <div>
                        <h2 className="text-white text-4xl mb-6 font-serif font-bold tracking-tight">Mossy</h2>
                        <div className="space-y-2 text-sm font-light opacity-70">
                            <p>서초구 반포대로 45 명정빌딩 3층</p>
                            <p>사업자등록번호: 123-45-67890</p>
                            <p>통신판매업신고: 2026-서울서초-0000</p>
                        </div>
                    </div>
                    <div className="text-right">
                        <div className="flex gap-8 justify-end mb-6 text-sm font-medium">
                            <a href="#" className="hover:text-white transition-colors">About Us</a>
                            <a href="#" className="hover:text-white transition-colors">Terms</a>
                            <a href="#" className="hover:text-white transition-colors">Privacy</a>
                            <a href="#" className="hover:text-white transition-colors">Help</a>
                        </div>
                        <p className="text-xs opacity-40 font-light tracking-wide">© 2026 Mossy Store. All rights reserved.</p>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default HomePage;
