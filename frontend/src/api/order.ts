import client, { type RsData } from './client';

export interface OrderCreatedResponse {
    orderId: number;
    orderNo: string;
    totalPrice: number;
}

export interface OrderItemRequest {
    productId: number;
    sellerId: number;
    productName: string;
    categoryName: string;
    price: number;
    weight: number;
    thumbnailUrl: string;
    quantity: number;
}

export interface CreateOrderRequest {
    totalPrice: number;
    paymentType: string;
    items: OrderItemRequest[];
}

export interface OrderResponse {
    orderId: number;
    orderNo: string;
    totalPrice: number;
    state: string;
    itemCount: number;
    address: string;
    createdAt: string;
}

export interface OrderDetailResponse {
    productId: number;
    quantity: number;
    orderPrice: number;
    sellerName: string;
}

export const orderApi = {
    createOrder: async (request: CreateOrderRequest) => {
        const response = await client.post<RsData<OrderCreatedResponse>>('/orders', request);
        return response.data;
    },

    getMyOrders: async (page = 0, size = 5) => {
        const response = await client.get<any>(`/orders?page=${page}&size=${size}`);
        return response.data;
    },

    getOrderDetails: async (orderId: number) => {
        // Backend returns List<OrderDetailResponse> directly or wrapped in RsData?
        // Assuming direct list based on controller check previously: public List<OrderDetailResponse> getOrder(...)
        const response = await client.get<OrderDetailResponse[]>(`/orders/${orderId}`);
        return response.data;
    }
};
