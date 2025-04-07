import React, {useEffect, useState} from "react";


export default function ErrorMessage({errorMessage}: { errorMessage: string | null }) {
    const [visible, setVisible] = useState<boolean>(true);

    // Reset visibility if a new error message is received.
    useEffect(() => {
        if (errorMessage) {
            setVisible(true);
        }
    }, [errorMessage]);

    if (!errorMessage || !visible) return null;

    return (
        <div
            className="position-fixed top-50 start-50 translate-middle"
            style={{zIndex: 1050}}
        >
            <div className="alert alert-danger d-flex justify-content-between align-items-center" role="alert">
                <span>{errorMessage}</span>
                <button
                    type="button"
                    className="btn-close"
                    aria-label="Close"
                    onClick={() => setVisible(false)}
                />
            </div>
        </div>
    );
};