'use client'

export default function LogoutButton() {
    const handleLogout = () => {
        // Nuke cookies manually by expiring them (last resort)
        document.cookie.split(";").forEach(cookie => {
            const name = cookie.split("=")[0].trim();
            document.cookie = `${name}=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;`;
        });

    };

    return (
        <button className="btn btn-primary" onClick={handleLogout}>
            Logout
        </button>
    );
}
