export default function LogoutButton() {
    const handleLogout = async () => {
        await fetch("/logout", {
            method: "GET",
            credentials: "include"
        });

        window.location.href = "/index.html";
    };

    return (
        <main>
            {/* Hidden logout form */}
            <form id="logoutForm" method="post" action="http://localhost:8080/logout">
                {/* No content needed, just a form to be submitted */}
            </form>

            <button className="btn btn-primary" onClick={handleLogout}>Logout</button>

        </main>
    )
}