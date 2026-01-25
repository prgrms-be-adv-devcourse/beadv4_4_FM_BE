import { useEffect, useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { FaShoppingCart, FaUser, FaWallet } from 'react-icons/fa';
import { authApi } from '../../api/auth';

const Navbar = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [scrolled, setScrolled] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        setIsLoggedIn(!!token);

        const handleScroll = () => {
            setScrolled(window.scrollY > 20);
        };
        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, [location]);

    const handleLogout = async () => {
        const refreshToken = localStorage.getItem('refreshToken');
        if (refreshToken) {
            try {
                await authApi.logout(refreshToken);
            } catch (e) {
                console.error("Logout failed", e);
            }
        }
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setIsLoggedIn(false);
        navigate('/login');
    };

    return (
        <nav
            className={`
                fixed top-6 left-1/2 -translate-x-1/2 z-50 
                w-[90%] max-w-6xl 
                rounded-full 
                border border-white/40 
                transition-all duration-300 ease-in-out
                ${scrolled
                    ? 'bg-white/90 backdrop-blur-xl shadow-lg py-2 px-6'
                    : 'bg-white/60 backdrop-blur-md shadow-sm py-3 px-8'
                }
            `}
        >
            <div className="flex justify-between items-center">
                <Link to="/" className="flex items-center gap-2 text-2xl font-serif font-bold text-primary-color tracking-tight hover:opacity-80 transition-opacity">
                    Mossy
                </Link>

                <div className="flex gap-8 items-center">
                    <Link to="/market" className="font-medium text-text-main hover:text-primary-color transition-colors text-sm uppercase tracking-wide">
                        Market
                    </Link>

                    <div className="flex gap-3 items-center">
                        {isLoggedIn && (
                            <Link to="/wallet" className="w-10 h-10 flex items-center justify-center rounded-full bg-white/50 hover:bg-white hover:text-primary-color text-text-muted transition-all border border-transparent hover:border-primary-color/20 hover:shadow-md">
                                <FaWallet size={16} />
                            </Link>
                        )}
                        <Link to="/cart" className="w-10 h-10 flex items-center justify-center rounded-full bg-white/50 hover:bg-white hover:text-primary-color text-text-muted transition-all border border-transparent hover:border-primary-color/20 hover:shadow-md">
                            <FaShoppingCart size={16} />
                        </Link>
                        <Link to="/mypage" className="w-10 h-10 flex items-center justify-center rounded-full bg-white/50 hover:bg-white hover:text-primary-color text-text-muted transition-all border border-transparent hover:border-primary-color/20 hover:shadow-md">
                            <FaUser size={16} />
                        </Link>

                        {isLoggedIn ? (
                            <button
                                onClick={handleLogout}
                                className="px-5 py-2 rounded-full text-sm font-medium bg-black/5 text-text-main hover:bg-black/10 transition-colors ml-2"
                            >
                                Log out
                            </button>
                        ) : (
                            <Link
                                to="/login"
                                className="px-6 py-2.5 rounded-full text-sm font-medium bg-primary-color text-white shadow-lg shadow-primary-color/20 hover:bg-primary-hover hover:shadow-xl hover:-translate-y-0.5 transition-all ml-2"
                            >
                                Log in
                            </Link>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
