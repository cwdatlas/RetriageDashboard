'use client'

import {getCookies} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role"
import React, {useEffect, useState} from "react";
import SelectEvent from "@/app/components/selectEvent";
import NurseWaitEventPage from "@/app/components/nurseWaitEventPage";
import GuestWaitPage from "@/app/components/guestWaitPage";
import Header from "@/app/components/header";
import Footer from "@/app/components/footer";
import ToggleButton from "@/app/components/toggleButton";

export default function EventViewer() {
    const role = getCookies("role") as Role;

    const [viewEvents, setViewEvents] = useState(true);

    function toggleEventView(){
        setViewEvents(!viewEvents);
    }

    return (
        <main>
            <Header/>
            {role === Role.Director && (
                <div>
                    <ToggleButton onToggle={toggleEventView} label={"Toggle Event Selection"}/>
                    {viewEvents && (<SelectEvent eventViewToggle={toggleEventView}/>)}
                </div>
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