export default function LogoutButton() {
    const domain = process.env.DOMAIN || 'localhost'
    const handleLogout = () => {
        const form = document.getElementById("logoutForm") as HTMLFormElement | null;
        form?.requestSubmit(); // Use requestSubmit() for modern behavior
    };

    return (
        <main>
            {/* Hidden logout form */}
            <form id="logoutForm" method="post" action={"https://" + domain + ":8430/logout"}>
                {/* No content needed, just a form to be submitted */}
            </form>

            <button className="btn btn-primary" onClick={handleLogout}>Logout</button>

        </main>
    )
}