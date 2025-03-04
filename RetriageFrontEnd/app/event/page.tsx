'use client'

import Header from "@/app/components/header";
import Footer from "@/app/components/footer";
import {getCookies} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role"
import Link from "next/link";

export default function event() {
    const role = getCookies("role");
    if (role === Role.Director) {
        return (
            <main>
                <Header/>
                <div>This is the Admin event page</div>
                <Link href={"/event/eventcreation"}>Create Event</Link>
                <Footer/>
            </main>
        )
    }else{
        return (
            <main>
                <Header/>
                <div>This is the event page</div>
                <Footer/>
            </main>
        )
    }
}