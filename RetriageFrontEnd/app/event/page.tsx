'use client'

import {getCookies} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role"
import React from "react";
import SelectEvent from "@/app/components/selectEvent";
import NurseWaitEventPage from "@/app/components/nurseWaitEventPage";
import GuestWaitPage from "@/app/components/guestWaitPage";
import Header from "@/app/components/header";
import Footer from "@/app/components/footer";

export default function EventViewer() {
    const role = getCookies("role") as Role;

    return (
        <main>
            <Header/>
            {role === Role.Director && (
                <SelectEvent/>
            )}
            {role === Role.Nuse && (
                <NurseWaitEventPage/>
            )}
            {role != Role.Director || role != Role.Director && (
                <GuestWaitPage/>
            )}
            <Footer/>
        </main>
    )
}