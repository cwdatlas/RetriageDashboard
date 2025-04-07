import React, {useEffect, useState} from "react";


export default function StatusMessage({statusMessage}: { statusMessage: string | null }) {
    const [visible, setVisible] = useState<boolean>(true);

    // Reset visibility if a new error message is received.
    useEffect(() => {
        if (statusMessage) {
            setVisible(true);
        }
    }, [statusMessage]);

    if (!statusMessage || !visible) return null;

    return (
        <div
            className="position-fixed top-50 start-50 translate-middle"
            style={{zIndex: 1050}}
        >
            <div className="alert alert-success d-flex justify-content-between align-items-center">
                <span>{statusMessage}</span>
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