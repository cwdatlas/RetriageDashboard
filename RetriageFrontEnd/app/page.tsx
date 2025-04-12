'use client'

import Header from "@/app/components/panel/header";
import Footer from "@/app/components/panel/footer";
import { useEffect, useState } from "react";
import { getUserByToken } from "@/app/api/userApi";
import { UserDto } from "@/app/models/userDto";

export default function Home() {
    const [user, setUser] = useState<UserDto | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        console.log("Calling getUserByToken...");
       void getUserByToken(setUser, setError); // It returns a promise. I do not care.
    }, []);
    return (
        <main className="d-flex flex-column min-vh-100">
            <Header/>

            <p>The Mass Mock Casualty Event is put on by Kathrin Pieper at the Carroll College nursing department.
                This website is to manage the data of the event and help nursing students get a better idea of what it
                means
                to work in a stressful and fast passed environment that is mimicking real nursing practices.</p>

            {user && (
                <p>Welcome, {user.username}! Your role is: {user.role}</p>
            )}
            {error && (
                <p style={{ color: "red" }}>{error}</p>
            )}


            <Footer/>
        </main>
    );
}
