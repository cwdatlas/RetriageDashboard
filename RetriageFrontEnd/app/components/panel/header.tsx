"use client";

import React, {useEffect, useState} from "react";
import Link from "next/link";
import LogoutButton from "@/app/components/buttons/logoutButton";
import {GetCookies} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role";

export default function Header() {
    // Set initial values to empty strings so that during the first render (hydration) they match.
    const [username, setUsername] = useState("");
    const [role, setRole] = useState("");

    useEffect(() => {
        // Now that we are on the client, read the cookies.
        const firstname = GetCookies("firstname")
        const lastname = GetCookies("lastname")
        const role: Role = GetCookies("role") as Role;
        if (firstname && lastname && role) {
            setUsername(firstname + " " + lastname);
            setRole(role);
        }
    }, []);

    return (
        <nav className="navbar navbar-expand-lg navbar-light bg-light sticky-top">
            <div className="container-fluid">
                {/* Branding */}
                <Link className="navbar-brand" href="/">
                    <img
                        src="/images/shield.png"
                        alt="Carroll College Shield"
                        style={{
                            width: "50px",
                            height: "50px",
                            display: "block",
                            margin: "0 auto",
                            borderRadius: "50%",
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
              {username} | {role}
            </span>
                        <LogoutButton/>
                    </div>
                </div>
            </div>
        </nav>
    );
}
