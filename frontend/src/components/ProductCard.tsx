import React from 'react';
import { Link } from 'react-router-dom';
import { FaLeaf } from 'react-icons/fa';
import type { ProductResponse } from '../api/market';

interface ProductCardProps {
    product: ProductResponse;
    onAddToCart: (e: React.MouseEvent, productId: number) => void;
}

export const ProductCard = ({ product, onAddToCart }: ProductCardProps) => {
    // 이미지 로드 성공 여부를 추적
    const [isLoaded, setIsLoaded] = React.useState(false);
    const [hasError, setHasError] = React.useState(false);

    return (
        <Link
            to={`/market/${product.productId}`}
            className="group block bg-surface-color rounded-2xl overflow-hidden border border-border-color hover:border-primary-color hover:shadow-xl transition-all duration-300 hover:-translate-y-1 h-full flex flex-col"
        >
            <div className="relative aspect-[4/5] bg-stone-50 overflow-hidden">
                {/* 1. 이미지 태그: S3 주소가 있을 때만 */}
                {product.thumbnail && !hasError && (
                    <img
                        src={product.thumbnail}
                        alt={product.name}
                        // crossOrigin="anonymous" // CORS 문제가 의심될 때 추가
                        className={`w-full h-full object-cover transition-opacity duration-500 group-hover:scale-105 ${
                            isLoaded ? 'opacity-100' : 'opacity-0'
                        }`}
                        onLoad={() => setIsLoaded(true)}
                        onError={() => {
                            console.error("S3 이미지 로드 실패:", product.thumbnail);
                            setHasError(true);
                        }}
                    />
                )}

                {/* 2. 대체 UI (로딩 중이거나, 에러 났거나, 주소가 없을 때) */}
                {(!isLoaded || hasError || !product.thumbnail) && (
                    <div className="absolute inset-0 flex flex-col items-center justify-center text-secondary-color bg-stone-100">
                        {hasError || !product.thumbnail ? (
                            <>
                                <FaLeaf className="text-4xl opacity-20 mb-2" />
                                <span className="text-xs font-medium uppercase tracking-widest opacity-40">No Image</span>
                            </>
                        ) : (
                            /* 로딩 애니메이션 */
                            <div className="w-full h-full bg-stone-200 animate-pulse" />
                        )}
                    </div>
                )}

                {/* 장바구니 버튼 (생략) */}
            </div>
            {/* 하단 정보 (생략) */}
        </Link>
    );
};
