import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';

const ErrorPage = () => (
    <div className="d-flex vh-100 vw-100 bg-light align-items-center justify-content-center">
        <div className="text-center p-4 rounded shadow bg-white">
            <h1 className="mb-3">Something went wrong</h1>
            <p className="mb-4 text-muted">
                We’re sorry for the inconvenience. Please try refreshing the page or come back later.
            </p>
            <button
                className="btn btn-primary"
                onClick={() => window.location.reload()}
            >
                Reload
            </button>
        </div>
    </div>
);

export default ErrorPage;
