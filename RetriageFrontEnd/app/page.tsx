'use client'

import Header from "@/app/components/header";
import Footer from "@/app/components/footer";
import {SetCookies} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role"

export default function Home() {
    return (
        <main>
            <Header/>

            <Footer/>
        </main>
    )
}
