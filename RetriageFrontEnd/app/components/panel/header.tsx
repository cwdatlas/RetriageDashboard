"use client";

import React from "react";
import Link from "next/link";
import {GetCookies} from "@/app/api/cookieApi";
import LogoutButton from "@/app/components/buttons/logoutButton";

export default function Header() {
    const username = GetCookies("firstName") + " " + GetCookies("lastName");
    const role = GetCookies("role");
    const job = GetCookies("job");
    return (
        <header>
            <div>
                <Link href="/">Home</Link>
                <button><Link href="/event">Current Event</Link></button>
            </div>

            <div>Username: {username}</div>
            <div>Role: {role}</div>
            <div>Job: {job}</div>
            <LogoutButton />
        </header>
    );
}
