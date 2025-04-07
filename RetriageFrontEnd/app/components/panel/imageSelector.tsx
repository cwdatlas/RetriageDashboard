import React from "react";

// List the image file names available in public/images.
// (This list may eventually be generated dynamically from the folder.)
const imageFiles = [
    "ambulance.png",
    "ICU.png",
    "medFloor.png",
    "OR.png",
    "peds.png",
    "surgical.png"
];

export default function ImageSelector({ icon, setIcon }: { icon: string; setIcon: (icon: string) => void }) {
    return (
        <div className="container my-4">
            <h3 className="mb-3 text-center">Choose an Image</h3>
            <div className="row">
                {imageFiles.map((fileName, index) => (
                    // Using a composite key in case file names repeat in the future.
                    <div key={`${fileName}-${index}`} className="col-md-3 col-sm-6 mb-3">
                        <div className={`card h-100 ${icon === fileName ? "border border-primary" : ""}`}>
                            <img
                                src={`/images/${fileName}`}
                                className="card-img-top"
                                alt={fileName}
                                style={{ objectFit: "cover", height: "150px" }}
                            />
                            <div className="card-body d-flex flex-column justify-content-center">
                                {/* Using type="button" so clicking does not submit any form.
                                    Also, using btn-danger for a red button with white text. */}
                                <button
                                    type="button"
                                    className="btn btn-danger w-100"
                                    onClick={() => setIcon(fileName)}
                                >
                                    Select
                                </button>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
