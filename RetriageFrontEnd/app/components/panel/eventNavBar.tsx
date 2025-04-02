// MiniNavBar component: a sticky nav bar with buttons and a countdown timer.
import {useEffect, useState} from "react";
import {GetCookies} from "@/app/api/cookieApi";
import {Role} from "@/app/enumerations/role";
import {Event} from "@/app/models/event";
import {Status} from "@/app/enumerations/status";
import ToggleButton from "@/app/components/buttons/toggleButton";
import CreatePatient from "@/app/components/buttons/createPatient";
import Link from "next/link";

export default function EventNavBar({
                        activeEvent,
                        toggleEventView,
                        getActiveEvent,
                    }: {
    activeEvent: Event | null;
    toggleEventView: () => void;
    getActiveEvent: () => Event;
}) {
    const [timeLeft, setTimeLeft] = useState<number>(activeEvent?.remainingDuration || 0);
    const [role] = useState(GetCookies("role") as Role);

    // When activeEvent updates (e.g. via a WebSocket update), sync the timer.
    useEffect(() => {
        if (activeEvent && activeEvent.status === Status.Running) {
            setTimeLeft(activeEvent.remainingDuration);
        }
    }, [activeEvent]);

// Local countdown that ticks every second.
    useEffect(() => {
        if (activeEvent && activeEvent.status === Status.Running) {
            const interval = setInterval(() => {
                setTimeLeft((prev) => {
                    const newTime = prev - 1000;
                    return newTime >= 0 ? newTime : 0;
                });
            }, 1000);
            return () => clearInterval(interval);
        }
    }, [activeEvent]);

    // Simple formatter to show mm:ss
    const formatTime = (ms: number) => {
        const totalSeconds = Math.floor(ms / 1000);
        const minutes = Math.floor(totalSeconds / 60);
        const seconds = totalSeconds % 60;
        return `${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-secondary sticky-top" style={{zIndex: 1050}}>
            <div className="container-fluid">
                {/* Left side: toggle event section button */}
                {role == Role.Director &&
                    <div className="d-flex">
                        <ToggleButton onToggle={toggleEventView} label={"Toggle Event Section"}/>
                    </div>}
                {/* Center: countdown timer */}
                <div className="mx-auto">
                    {activeEvent && activeEvent.status === Status.Running ? (
                        <span className="navbar-text h5 mb-0">{formatTime(timeLeft)}</span>
                    ) : (
                        <span className="navbar-text h5 mb-0">Event Not Running</span>
                    )}
                </div>
                {/* Right side: Create Patient (if event running) and Create Event */}
                <div className="d-flex">
                    {activeEvent && activeEvent.status === Status.Running && (
                        <div className="me-2">
                            {activeEvent.status === Status.Running && role != Role.Guest &&
                                <CreatePatient getActiveEvent={getActiveEvent}/>}
                        </div>
                    )}
                    <div>
                        {role == Role.Director && <Link className="btn btn-primary" href="/event/eventcreation">
                            Create Event
                        </Link>}
                    </div>
                </div>
            </div>
        </nav>
    );
}