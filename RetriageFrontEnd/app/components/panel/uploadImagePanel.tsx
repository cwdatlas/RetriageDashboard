export default function UploadImagePanel() {

    return (
        <main>
            <h2>Upload an Image</h2>
            <form action="http://localhost:8080/uploadImage" method="post" encType="multipart/form-data">
                <div>
                    <label htmlFor="image">Choose an image to upload:</label>
                    <input type="file" id="image" name="image" required/>
                </div>
                <br/>
                <button type="submit">Upload</button>
            </form>
        </main>
    )
}