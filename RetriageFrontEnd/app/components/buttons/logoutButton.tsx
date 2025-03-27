export default function LogoutButton() {
    const handleLogout = () => {
        document.getElementById("logoutForm")?.submit(); // TODO fix submit method
    };

    return (
        <main>
            {/* Hidden logout form */}
            <form id="logoutForm" method="post" action="http://localhost:8080/logout">
                {/* No content needed, just a form to be submitted */}
            </form>

            <button onClick={handleLogout}>Logout</button>

        </main>
    )
}