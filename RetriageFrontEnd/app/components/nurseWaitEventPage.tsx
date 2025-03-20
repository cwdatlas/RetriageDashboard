"use client";
import Header from "@/app/components/header";
import Footer from "@/app/components/footer";
import React from "react";

export default function NurseWaitEventPage() {
    return (
        <main>
            <Header/>
            <div>This is the event page</div>
            There is no active event for you to participate in.
            <Footer/>
        </main>
    )
}