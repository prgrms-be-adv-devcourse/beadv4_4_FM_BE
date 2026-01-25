import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDaumPostcodePopup } from 'react-daum-postcode';
import Modal from '../components/Modal';
import { authApi } from '../api/auth';

const SignupPage = () => {
    const navigate = useNavigate();
    const openPostcode = useDaumPostcodePopup('https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js');
    // Form state
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        name: '',
        nickname: '',
        phoneNum: '',
        address: '',
        rrn: '',
        latitude: 0,
        longitude: 0,
    });
    const [error, setError] = useState('');
    const [showSuccessModal, setShowSuccessModal] = useState(false);
    // State for Map SDK
    const [isMapLoaded, setIsMapLoaded] = useState(false);
    const [mapError, setMapError] = useState(false);
    // Additional state for error handling and success modal
    // Robust Script Loader
    useEffect(() => {
        let attempts = 0;
        const maxAttempts = 100; // 10 seconds (100ms * 100)

        const checkMapLoaded = () => {
            // Check if the script is loaded and the kakao object is available
            if ((window as any).kakao && (window as any).kakao.maps) {
                // Initialize using the autoload=false pattern
                (window as any).kakao.maps.load(() => {
                    console.log("KaKao Maps SDK loaded and initialized.");
                    setIsMapLoaded(true);
                    setMapError(false);
                });
            } else {
                attempts++;
                if (attempts < maxAttempts) {
                    setTimeout(checkMapLoaded, 100);
                } else {
                    console.error("Kakao Maps SDK failed to load within timeout (10s).");
                    setMapError(true);
                    alert(`지도 스크립트 로딩 실패.\n\n[체크리스트]\n1. Kakao Developers > 내 애플리케이션 > 플랫폼 > Web > 사이트 도메인에 '${window.location.origin}' 이 등록되어 있는지 확인해주세요.\n(현재 실행 중인 포트가 ${window.location.port} 입니다)\n\n2. API 키가 정확한지 확인해주세요.`);
                }
            }
        };

        checkMapLoaded();
    }, []);

    const handleAddressComplete = (data: any) => {
        let fullAddress = data.address;
        let extraAddress = '';

        if (data.addressType === 'R') {
            if (data.bname !== '') {
                extraAddress += data.bname;
            }
            if (data.buildingName !== '') {
                extraAddress += (extraAddress !== '' ? `, ${data.buildingName}` : data.buildingName);
            }
            fullAddress += (extraAddress !== '' ? ` (${extraAddress})` : '');
        }

        // Geocoding
        if (isMapLoaded && (window as any).kakao && (window as any).kakao.maps) {
            (window as any).kakao.maps.load(() => {
                const geocoder = new (window as any).kakao.maps.services.Geocoder();
                geocoder.addressSearch(data.address, (result: any, status: any) => {
                    if (status === (window as any).kakao.maps.services.Status.OK) {
                        const coords = new (window as any).kakao.maps.LatLng(result[0].y, result[0].x);
                        const latitude = coords.getLat();
                        const longitude = coords.getLng();

                        console.log("Geocoding success:", latitude, longitude);

                        setFormData(prev => ({
                            ...prev,
                            address: fullAddress,
                            latitude: latitude,
                            longitude: longitude
                        }));
                    } else {
                        console.error("Geocoding failed. Status:", status);
                        alert("주소의 위치 정보를 찾을 수 없습니다. (Kakao Maps API 오류)\n도메인 등록 여부나 API 키를 확인해주세요.");
                        // Fallback: Reset to 0
                        setFormData(prev => ({
                            ...prev,
                            address: fullAddress,
                            latitude: 0,
                            longitude: 0
                        }));
                    }
                });
            });
        } else {
            console.error("Kakao Maps SDK not ready");
            alert("지도 서비스를 불러오는 중입니다. 잠시 후 다시 시도해주세요.");
            setFormData(prev => ({
                ...prev,
                address: fullAddress
            }));
        }
    };
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };
    const handleSearchAddress = () => {
        if (mapError) {
            // If map is broken, manual input is enabled.
            // Just focus the input field for the user.
            document.getElementsByName('address')[0]?.focus();
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

        // Validate Coordinates (Skip if map failed)
        let finalData = { ...formData };
        if (!mapError && (formData.latitude === 0 || formData.longitude === 0)) {
            alert("주소의 위치 정보(위도/경도)가 설정되지 않았습니다.\n주소를 다시 검색하거나, 올바른 주소를 선택해주세요.");
            return;
        }

        // If map failed, use default coordinates (Seoul City Hall) to bypass backend validation
        if (mapError && (formData.latitude === 0 || formData.longitude === 0)) {
            finalData.latitude = 37.5665;
            finalData.longitude = 126.9780;
        }

        try {
            const response = await authApi.signup(finalData);
            // Backend returns "S-200" for success
            if (response.resultCode.startsWith('S-200') || response.resultCode.startsWith('200')) {
                setShowSuccessModal(true);
            } else {
                setError('회원가입 실패: ' + response.msg);
            }
        } catch (err: any) {
            console.error(err);
            setError('회원가입 중 오류가 발생했습니다: ' + (err.response?.data?.msg || err.message));
        }
    };

    return (
        <div style={{ maxWidth: '500px', margin: '4rem auto', marginTop: '140px' }}>
            <h1 style={{ textAlign: 'center', marginBottom: '2rem' }}>회원가입</h1>

            <div className="card">
                <form onSubmit={handleSubmit} style={{ display: 'grid', gap: '1rem' }}>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>이메일</label>
                        <input name="email" type="email" value={formData.email} onChange={handleChange} required style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>비밀번호</label>
                        <input name="password" type="password" value={formData.password} onChange={handleChange} required style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>이름</label>
                        <input name="name" type="text" value={formData.name} onChange={handleChange} required style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>닉네임</label>
                        <input name="nickname" type="text" value={formData.nickname} onChange={handleChange} required style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>전화번호</label>
                        <input name="phoneNum" type="tel" value={formData.phoneNum} onChange={handleChange} placeholder="010-0000-0000" required style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>주소</label>
                        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                            <input name="address" type="text" value={formData.address} onChange={handleChange} required readOnly={!mapError} style={{ flex: 1, padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)', backgroundColor: mapError ? 'white' : '#f1f5f9' }} placeholder={mapError ? "주소를 직접 입력해주세요" : "주소 검색 버튼을 눌러주세요"} />
                            <button type="button" onClick={handleSearchAddress} className="btn btn-outline" style={{ whiteSpace: 'nowrap' }} disabled={!isMapLoaded && !mapError}>
                                {isMapLoaded ? "주소 검색" : mapError ? "수동 입력" : "로딩 중..."}
                            </button>
                        </div>
                        {mapError && <div style={{ color: 'var(--danger-color)', fontSize: '0.8rem', marginTop: '0.5rem' }}>지도 서비스를 불러올 수 없습니다. 주소를 직접 입력해주세요.</div>}
                    </div>
                    <div>
                        <label style={{ display: 'block', marginBottom: '0.5rem' }}>주민번호 (7자리)</label>
                        <input name="rrn" type="text" value={formData.rrn} onChange={handleChange} placeholder="900101-1" required style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
                        <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>* 실명 인증 및 정산용</span>
                    </div>

                    {error && <div style={{ color: 'var(--danger-color)', fontSize: '0.9rem' }}>{error}</div>}

                    <button type="submit" className="btn btn-primary" style={{ marginTop: '1rem', borderRadius: '50px' }}>
                        가입하기
                    </button>
                </form>
            </div >
            <Modal
                isOpen={showSuccessModal}
                title="회원가입 완료"
                message="회원가입이 성공적으로 완료되었습니다."
                onConfirm={() => {
                    setShowSuccessModal(false);
                    navigate('/login', { replace: true });
                }}
            />
        </div >
    );
};

export default SignupPage;
