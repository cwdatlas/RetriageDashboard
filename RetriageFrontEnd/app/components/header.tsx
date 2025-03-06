"use client";

import React from "react";
import Link from "next/link";
import {getCookies} from "@/app/api/cookieApi";

export default function Header() {
    const username = getCookies("username")
    const role = getCookies("role")
    return (
        <header>
            <div>
                <Link href="/">
                    <img
                        src="/app/images/Shield.PNG"
                        alt="Home Icon"
                    />
                </Link>
                <button><Link href="/event">Current Event</Link></button>
            </div>

            <div>Username: {username}</div>
            <div>Role: {role}</div>
        </header>
    );
}
