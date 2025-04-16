"use client";

import React, {useEffect, useState} from "react";
import Link from "next/link";
import LogoutButton from "@/app/components/buttons/logoutButton";
import {UserDto} from "@/app/models/userDto";
import {getUserByToken} from "@/app/api/userApi";
import ErrorMessage from "@/app/components/modals/errorMessage";

export default function Header() {
    // Set initial values to empty strings so that during the first render (hydration) they match.
    const [user, setUser] = useState<UserDto | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        void getUserByToken(setUser, setError);
    }, []);

    return (
        <nav className="navbar navbar-expand-lg navbar-light bg-light">
            <div className="container-fluid">
                <ErrorMessage errorMessage={error}/>
                {/* Branding */}
                <Link className="navbar-brand" href="/">
                    <img
                        src="/images/navBranding.png"
                        className="img-thumbnail"
                        alt="Carroll College Nursing Program Logo"
                        style={{
                            height: "50px"
                        }}
                    />
                </Link>

                {/* Toggler for mobile view */}
                <button
                    className="navbar-toggler"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#navbarSupportedContent"
                    aria-controls="navbarSupportedContent"
                    aria-expanded="false"
                    aria-label="Toggle navigation"
                >
                    <span className="navbar-toggler-icon"/>
                </button>

                {/* Navbar links and user info */}
                <div className="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                        <li className="nav-item">
                            <Link className="nav-link" href="/">
                                Home
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link className="nav-link" href="/event">
                                Current Event
                            </Link>
                        </li>
                    </ul>
                    <div className="d-flex align-items-center">
            <span className="navbar-text me-3">
              {user?.username} | {user?.role}
            </span>
                        <LogoutButton/>
                    </div>
                </div>
            </div>
        </nav>
    );
}
