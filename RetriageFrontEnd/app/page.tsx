'use client'

import Header from "@/app/components/panel/header";
import Footer from "@/app/components/panel/footer";

export default function Home() {
    return (
        <main className="d-flex flex-column min-vh-100">
            <Header/>

            <p>The Mass Mock Casualty Event is put on by Kathrin Pieper at the Carroll College nursing department.
                This website is to manage the data of the event and help nursing students get a better idea of what it
                means
                to work in a stressful and fast passed environment that is mimicking real nursing practices.</p>

            <Footer/>
        </main>
    );
}
