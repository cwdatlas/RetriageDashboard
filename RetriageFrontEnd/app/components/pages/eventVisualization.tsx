import {Event} from '@/app/models/event'
import {PoolType} from "@/app/enumerations/poolType";
import BayPanel from "@/app/components/panel/bayPannel";
import {useState} from "react";
import {DndContext, DragEndEvent, DragOverEvent, DragStartEvent} from "@dnd-kit/core";
import {Patient} from "@/app/models/patient";
import {sendEvent} from "@/app/api/eventWebSocket";

export default function EventVisualization({getActiveEvent}: { getActiveEvent: () => Event }) {
    const event = getActiveEvent();
    const [originPoolId, setOriginPoolIdId] = useState<number | null>(null);
    const [error, setError] = useState<string | null>(null);
    return (
        <DndContext onDragEnd={handleDragEnd} onDragStart={handleDragStart} onDragOver={handleDragOver}>
            <h2>{event.name}</h2>
            {error && (
                <p>{error}</p>
            )}
            <div className="row">
                {/* First Column: Bay Panels */}
                <div className="col">
                    {event.pools.map((template, idx) => {
                        return (
                            template.poolType === PoolType.Bay && (
                                <div key={idx}>
                                    <BayPanel bay={template}/>
                                </div>
                            )
                        )
                    })}
                </div>
                {/* Second Column: MedService Panels */}
                <div className="col">
                    {event.pools.map((template, idx) => {
                        return (
                            template.poolType === PoolType.MedService && (
                                <div key={idx}>
                                    <BayPanel bay={template}/>
                                </div>
                            )
                        )
                    })}
                </div>
            </div>
        </DndContext>
    )

    function handleDragEnd(event: DragEndEvent) {
        const over = event.over;
        if (over && originPoolId && originPoolId !== over.id) {
            console.log("droppable", event.over);
            console.log("draggable", event.active.id);
            console.log("origin", originPoolId);

            const overPool = getActiveEvent().pools.find(pool => pool.id === over.id);
            const originPool = getActiveEvent().pools.find(pool => pool.id === originPoolId);
            if (overPool && originPool) {
                const patient = originPool.patients.find(patient => patient.id === event.active.id);
                if (!overPool.patients && patient) {
                    const newPatientArray: Patient[] = new Array(1);
                    // Adding patient to new array
                    newPatientArray[0] = patient;
                    overPool.patients = newPatientArray;
                    // Deleting patient from origin pool
                    const index: number = originPool.patients.findIndex(storedPatient => storedPatient.id === patient.id)
                    if (index !== -1) {
                        originPool.patients.splice(index, 1);
                        sendEvent(getActiveEvent());
                    } else {
                        setError("Patient not found")
                    }
                } else if (patient) {
                    overPool.patients.push(patient);
                    // Deleting patient from origin pool
                    const index: number = originPool.patients.findIndex(storedPatient => storedPatient.id === patient.id)
                    if (index !== -1) {
                        originPool.patients.splice(index, 1);
                        sendEvent(getActiveEvent());
                    } else {
                        setError("Patient not found")
                    }
                }
            } else {
                setError("Selected Patient Pool not found.")
            }
        }
    }

    function handleDragStart(event: DragStartEvent) {
        console.log("start", event.active.id)
        setOriginPoolIdId(null)
    }

    function handleDragOver(event: DragOverEvent) {
        console.log("over", event.active.id)
        if (!originPoolId && event.over) {
            if (typeof event.over.id === "string") {
                setOriginPoolIdId(parseInt(event.over.id))
            } else {
                setOriginPoolIdId(event.over.id)
            }
        }
    }
}
