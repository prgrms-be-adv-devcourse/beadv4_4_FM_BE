import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDaumPostcodePopup } from 'react-daum-postcode';
import { memberApi, type SellerRequestCreateRequest } from '../api/member';
import Modal from '../components/Modal';

const SellerRequestPage = () => {
    const navigate = useNavigate();
    const openPostcode = useDaumPostcodePopup('https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js');

    const [formData, setFormData] = useState<SellerRequestCreateRequest>({
        sellerType: "INDIVIDUAL",
        storeName: '',
        businessNum: '',
        representativeName: '',
        contactEmail: '',
        contactPhone: '',
        address1: '',
        address2: '',
        latitude: 0,
        longitude: 0,
    });

    const [isMapLoaded, setIsMapLoaded] = useState(false);
    const [mapError, setMapError] = useState(false);
    const [error, setError] = useState('');
    const [showSuccessModal, setShowSuccessModal] = useState(false);

    useEffect(() => {
        const scriptId = 'kakao-map-script';
        const existingScript = document.getElementById(scriptId);

        if (existingScript) {
            if ((window as any).kakao && (window as any).kakao.maps) {
                setIsMapLoaded(true);
            }
            return;
        }

        const script = document.createElement('script');
        script.id = scriptId;
        script.src = 'https://dapi.kakao.com/v2/maps/sdk.js?appkey=5dd5ef04cbba5202b3410c0e76f2f8c5&libraries=services&autoload=false';
        script.async = true;

        script.onload = () => {
            (window as any).kakao.maps.load(() => {
                setIsMapLoaded(true);
            });
        };

        script.onerror = () => {
            console.error('Failed to load Kakao Maps SDK');
            setMapError(true);
        };

        document.head.appendChild(script);
    }, []);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleAddressComplete = (data: any) => {
        let fullAddress = data.address;
        let extraAddress = '';

        if (data.addressType === 'R') {
            if (data.bname !== '') extraAddress += data.bname;
            if (data.buildingName !== '') extraAddress += (extraAddress !== '' ? `, ${data.buildingName}` : data.buildingName);
            fullAddress += (extraAddress !== '' ? ` (${extraAddress})` : '');
        }

        if (isMapLoaded && (window as any).kakao && (window as any).kakao.maps) {
            const geocoder = new (window as any).kakao.maps.services.Geocoder();
            geocoder.addressSearch(data.address, (result: any, status: any) => {
                if (status === (window as any).kakao.maps.services.Status.OK) {
                    const coords = new (window as any).kakao.maps.LatLng(result[0].y, result[0].x);
                    setFormData(prev => ({
                        ...prev,
                        address1: fullAddress,
                        latitude: coords.getLat(),
                        longitude: coords.getLng()
                    }));
                } else {
                    alert("주소의 위치 정보를 찾을 수 없습니다. (Kakao Maps API 오류)");
                    setFormData(prev => ({ ...prev, address1: fullAddress, latitude: 0, longitude: 0 }));
                }
            });
        } else {
            // Map failed but address text is valid
            setFormData(prev => ({ ...prev, address1: fullAddress }));
        }
    };

    const handleSearchAddress = () => {
        if (mapError) {
            document.getElementsByName('address1')[0]?.focus();
            return;
        }
        if (!isMapLoaded) {
            alert("지도 서비스를 불러오는 중입니다. 잠시만 기다려주세요.");
            return;
        }
        openPostcode({ onComplete: handleAddressComplete });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        let finalData = { ...formData };

        // Manual Fallback for Map Error
        if (mapError && (finalData.latitude === 0 || finalData.longitude === 0)) {
            finalData.latitude = 37.5665;
            finalData.longitude = 126.9780;
        }

        try {
            const response = await memberApi.requestSeller(finalData);
            if (response.resultCode.startsWith('S-200') || response.resultCode.startsWith('200')) {
                setShowSuccessModal(true);
            } else {
                setError('신청 실패: ' + response.msg);
            }
        } catch (err: any) {
            console.error(err);
            setError('오류 발생: ' + (err.response?.data?.msg || err.message));
        }
    };

    return (
        <div style={{ maxWidth: '600px', margin: '4rem auto', marginTop: '140px' }}>
            <h1 style={{ textAlign: 'center', marginBottom: '2rem' }}>판매자 신청</h1>

            <div className="card">
                <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '1rem' }}>

                    {/* Seller Type - Select */}
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 500 }}>판매자 유형</label>
                        <select
                            name="sellerType"
                            value={formData.sellerType}
                            onChange={handleChange}
                            style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)', backgroundColor: 'white', appearance: 'none', backgroundImage: 'url("data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23007CB2%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-5%200-9.3%201.8-12.9%205.4A17.6%2017.6%200%200%200%200%2082.2c0%205%201.8%209.3%205.4%2012.9l128%20127.9c3.6%203.6%207.8%205.4%2012.8%205.4s9.2-1.8%2012.8-5.4L287%2095c3.5-3.5%205.4-7.8%205.4-12.8%200-5-1.9-9.2-5.5-12.8z%22%2F%3E%3C%2Fsvg%3E")', backgroundRepeat: 'no-repeat', backgroundPosition: 'right .7em top 50%', backgroundSize: '.65em auto' }}
                        >
                            <option value="INDIVIDUAL">개인 판매자 (INDIVIDUAL)</option>
                            <option value="BUSINESS">사업자 판매자 (BUSINESS)</option>
                        </select>
                    </div>

                    {/* Store Info */}
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>상호명 (Store Name)</label>
                        <input name="storeName" value={formData.storeName} onChange={handleChange} required style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>사업자 등록번호</label>
                        <input name="businessNum" value={formData.businessNum} onChange={handleChange} required placeholder="000-00-00000" style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>대표자명</label>
                        <input name="representativeName" value={formData.representativeName} onChange={handleChange} required style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
                    </div>

                    <hr style={{ margin: '1rem 0', border: 0, borderTop: '1px solid var(--border-color)' }} />

                    {/* Contact Info */}
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>연락처 이메일</label>
                        <input name="contactEmail" type="email" value={formData.contactEmail} onChange={handleChange} required style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>연락처 전화번호</label>
                        <input name="contactPhone" type="tel" value={formData.contactPhone} onChange={handleChange} required placeholder="010-0000-0000" style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
                    </div>

                    <hr style={{ margin: '1rem 0', border: 0, borderTop: '1px solid var(--border-color)' }} />

                    {/* Address */}
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>사업장 주소</label>
                        <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.5rem' }}>
                            <input name="address1" value={formData.address1} onChange={handleChange} required readOnly={!mapError} placeholder="기본 주소" style={{ flex: 1, padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)', backgroundColor: mapError ? 'white' : '#f1f5f9' }} />
                            <button type="button" onClick={handleSearchAddress} className="btn btn-outline" style={{ whiteSpace: 'nowrap' }} disabled={!isMapLoaded && !mapError}>
                                {isMapLoaded ? "주소 검색" : mapError ? "수동 입력" : "로딩 중..."}
                            </button>
                        </div>
                        {mapError && <div style={{ color: 'var(--danger-color)', fontSize: '0.8rem', marginBottom: '0.5rem' }}>⚠️ 지도 API 설정(키/도메인) 문제로 자동 입력이 불가능합니다. 주소를 직접 입력해주세요.</div>}

                        <input name="address2" value={formData.address2} onChange={handleChange} required placeholder="상세 주소 (예: 101호)" style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
                    </div>

                    {error && <div style={{ color: 'var(--danger-color)', fontSize: '0.9rem' }}>{error}</div>}

                    <button type="submit" className="btn btn-primary" style={{ marginTop: '1rem' }}>
                        신청하기
                    </button>
                </form>
            </div>

            <Modal
                isOpen={showSuccessModal}
                title="신청 완료"
                message="판매자 신청이 성공적으로 접수되었습니다.\n관리자 승인 후 판매 활동이 가능합니다."
                onConfirm={() => {
                    setShowSuccessModal(false);
                    navigate('/mypage');
                }}
            />
        </div>
    );
};

export default SellerRequestPage;
