import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';

const Layout = () => {
    return (
        <div className="flex flex-col min-h-screen bg-background-color text-text-main font-sans">
            <Navbar />

            <main className="flex-1 w-full relative">
                <div className="w-full h-full">
                    <Outlet />
                </div>
            </main>

            <footer className="bg-surface-color border-t border-border-color py-8 mt-auto">
                <div className="container mx-auto px-4 text-center">
                    <p className="text-text-muted text-sm font-medium">
                        &copy; {new Date().getFullYear()} Mossy Store. All rights reserved.
                    </p>
                </div>
            </footer>
        </div>
    );
};

export default Layout;
