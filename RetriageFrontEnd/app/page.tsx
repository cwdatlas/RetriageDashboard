'use client'

import Header from "@/app/components/header";
import Footer from "@/app/components/footer";
import {SetCookie} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role"

export default function Home() {
    SetCookie("username", "Aidan Scott")
    SetCookie("role", Role.Director)
    return (
        <main>
            <Header/>

            <Footer/>
        </main>
    )
}
