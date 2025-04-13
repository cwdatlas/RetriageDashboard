import {useEffect, useState} from "react";
import {Role} from "@/app/enumerations/role";
import {Event} from "@/app/models/event";
import {Status} from "@/app/enumerations/status";
import ToggleButton from "@/app/components/buttons/toggleButton";
import CreatePatient from "@/app/components/buttons/createPatient";
import Link from "next/link";
import {UserDto} from "@/app/models/userDto";
import {getUserByToken} from "@/app/api/userApi";
import ErrorMessage from "@/app/components/modals/errorMessage";

export default function EventNavBar({
                                        activeEvent,
                                        toggleEventView,
                                        getActiveEvent,
                                    }: {
    activeEvent: Event | null;
    toggleEventView: () => void;
    getActiveEvent: () => Event;
}) {
    const [timeLeft, setTimeLeft] = useState<number>(0);
    const [error, setError] = useState<string | null>(null);
    const [user, setUser] = useState<UserDto | null>(null);

    // Fetch the active event when the component mounts.
    useEffect(() => {
        void getUserByToken(setUser, setError);
    }, []);

    // When activeEvent updates, immediately recalc time left.
    useEffect(() => {
        if (activeEvent && activeEvent.status === Status.Running && activeEvent.timeOfStatusChange) {
            const now = Date.now();
            const start = new Date(activeEvent.timeOfStatusChange).getTime();
            setTimeLeft(Math.max(activeEvent.remainingDuration - (now - start), 0));
        }
    }, [activeEvent]);

    // Recalculate the time left every second using the onStatusChange.
    useEffect(() => {
        if (activeEvent && activeEvent.status === Status.Running && activeEvent.timeOfStatusChange) {
            const interval = setInterval(() => {
                const now = Date.now();
                const start = new Date(activeEvent.timeOfStatusChange).getTime();
                const calculated = activeEvent.remainingDuration - (now - start);
                setTimeLeft(calculated > 0 ? calculated : 0);
            }, 1000);
            return () => clearInterval(interval);
        }
    }, [activeEvent]);

    // Simple formatter to show mm:ss.
    const formatTime = (ms: number) => {
        const totalSeconds = Math.floor(ms / 1000);
        const minutes = Math.floor(totalSeconds / 60);
        const seconds = totalSeconds % 60;
        return `${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-secondary sticky-top" style={{zIndex: 1050}}>
            <div className="container-fluid">
                <ErrorMessage errorMessage={error}/>
                {/* Left side: toggle event section button */}
                {user?.role === Role.Director && (
                    <div className="d-flex">
                        <ToggleButton onToggle={toggleEventView} label={"Toggle Event Section"}/>
                    </div>
                )}
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
                            {user?.role !== Role.Guest && <CreatePatient getActiveEvent={getActiveEvent}/>}
                        </div>
                    )}
                    <div>
                        {user?.role === Role.Director && (
                            <Link className="btn btn-primary" href="/event/eventcreation">
                                Create Event
                            </Link>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
}
