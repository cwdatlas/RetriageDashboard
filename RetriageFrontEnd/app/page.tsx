'use client'

import Header from "@/app/components/header";
import Footer from "@/app/components/footer";

export default function Home() {
    const handleLogout = () => {
        document.getElementById("logoutForm")?.submit(); //Says `submit` doesn't exist, but it works?
    };

    return (
        <main>
            <Header/>

            <h2>Upload an Image</h2>
            <form action="http://localhost:8080/uploadImage" method="post" encType="multipart/form-data">
                <div>
                    <label htmlFor="image">Choose an image to upload:</label>
                    <input type="file" id="image" name="image" required/>
                </div>
                <br/>
                <button type="submit">Upload</button>
            </form>

            <br/>

            {/* Hidden logout form */}
            <form id="logoutForm" method="post" action="http://localhost:8080/logout">
                {/* No content needed, just a form to be submitted */}
            </form>

            <button onClick={handleLogout}>Logout</button>

            <Footer/>
        </main>
    );
}
