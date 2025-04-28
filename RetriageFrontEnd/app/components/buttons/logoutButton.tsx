import Cookies from "js-cookie";

export default function LogoutButton() {
    const domain = Cookies.get("domain") || "localhost"

    const handleLogout = () => {
        const form = document.getElementById("logoutForm") as HTMLFormElement | null;
        form?.requestSubmit(); // Use requestSubmit() for modern behavior
    };

    return (
        <main>
            {/* Hidden logout form */}
            <form id="logoutForm" method="post" action={"https://" + domain + "/logout"}>
                {/* No content needed, just a form to be submitted */}
            </form>

            <button className="btn btn-primary" onClick={handleLogout}>Logout</button>

        </main>
    )
}