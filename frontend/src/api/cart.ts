import client, { type RsData } from './client';
import { getUserIdFromToken } from '../utils/auth';

export interface CartItem {
    id: number;
    productId: number;
    sellerId: number;
    productName: string;
    price: number; // matched with API
    quantity: number;
    thumbnailUrl: string | null; // matched with API
    categoryName?: string;
    weight?: number;
}

export interface CartResponse {
    id: number;
    userId: number;
    items: CartItem[];
    totalPrice: number;
}

export const cartApi = {
    getCart: async () => {
        const userId = getUserIdFromToken();
        if (!userId) throw new Error("로그인이 필요합니다.");

        const response = await client.get<RsData<CartResponse>>('/cart', {
            params: { userId }
        });
        return response.data;
    },

    addToCart: async (productId: number, quantity: number = 1) => {
        const userId = getUserIdFromToken();
        if (!userId) throw new Error("로그인이 필요합니다.");

        const response = await client.post<RsData<void>>('/cart/items', {
            productId,
            quantity
        }, {
            params: { userId }
        });
        return response.data;
    },

    removeFromCart: async (productId: number) => {
        const userId = getUserIdFromToken();
        if (!userId) throw new Error("로그인이 필요합니다.");

        const response = await client.delete<RsData<void>>(`/cart/items/${productId}`, {
            params: { userId }
        });
        return response.data;
    },

    updateCartItem: async (productId: number, quantity: number) => {
        const userId = getUserIdFromToken();
        if (!userId) throw new Error("로그인이 필요합니다.");

        const response = await client.patch<RsData<void>>('/cart/items', {
            productId,
            quantity
        }, {
            params: { userId }
        });
        return response.data;
    },

    clearCart: async () => {
        const userId = getUserIdFromToken();
        if (!userId) throw new Error("로그인이 필요합니다.");

        const response = await client.delete<RsData<void>>('/cart', {
            params: { userId }
        });
        return response.data;
    }
};
