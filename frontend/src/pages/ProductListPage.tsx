import { useEffect, useState } from 'react';
import { marketApi, type ProductResponse } from '../api/market';
import { cartApi } from '../api/cart';
import { ProductCard } from '../components/ProductCard';

const ProductListPage = () => {
    const [products, setProducts] = useState<ProductResponse[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const data = await marketApi.getProducts();
                setProducts(data.data.content);
            } catch (error) {
                console.error('Failed to fetch products', error);
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, []);

    const addToCart = async (e: React.MouseEvent, productId: number) => {
        e.preventDefault();
        try {
            await cartApi.addToCart(productId, 1);
            if (window.confirm('장바구니에 담겼습니다. 장바구니로 이동하시겠습니까?')) {
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

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-[60vh] mt-20">
                <div className="text-lg text-text-muted animate-pulse">상품 목록을 불러오는 중...</div>
            </div>
        );
    }

    return (
        <div className="bg-background-color min-h-screen pt-36 pb-20">
            <div className="container mx-auto px-4 max-w-[1200px]">
                <header className="mb-16 text-center">
                    <span className="text-accent-color font-bold tracking-widest uppercase text-sm mb-4 block animate-fade-in-up">Our Collection</span>
                    <h1 className="text-4xl md:text-5xl font-serif font-bold text-text-main mb-6 animate-fade-in-up delay-100">Market</h1>
                    <p className="text-text-muted max-w-2xl mx-auto text-lg leading-relaxed animate-fade-in-up delay-200">
                        자연에서 온 영감, 지속 가능한 삶을 위한 엄선된 제품들을 만나보세요.
                    </p>
                </header>

                {products.length === 0 ? (
                    <div className="text-center py-20 bg-surface-color/50 backdrop-blur-sm rounded-2xl border border-border-color animate-fade-in-up delay-300">
                        <p className="text-text-muted text-lg">등록된 상품이 없습니다.</p>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
                        {products.map((product, index) => (
                            <div key={product.productId} className="animate-fade-in-up" style={{ animationDelay: `${index * 50}ms` }}>
                                <ProductCard
                                    product={product}
                                    onAddToCart={addToCart}
                                />
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ProductListPage;
