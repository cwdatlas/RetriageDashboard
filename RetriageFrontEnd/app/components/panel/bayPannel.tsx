'use client'

import {PatientPool} from "@/app/models/patientPool";
import PatientIcon from "@/app/components/panel/patientIcon";
import {useDroppable} from "@dnd-kit/core";
import {Event} from "@/app/models/event";

export default function BayPanel({bay, getActiveEvent}: { bay: PatientPool, getActiveEvent: () => Event }) {
    const {isOver, setNodeRef} = useDroppable({
        id: bay.id || 0,
    });
    const style = {
        color: isOver ? 'green' : undefined,
    };
    return (
        <div ref={setNodeRef} style={style} className="card rounded mb-3">
            <div className="card-header">
                <h2 className="card-title">{bay.name}</h2>
            </div>
            <div className="card-body">
                <div className="row row-cols-6">
                    {bay.patients.map((patient, idx) => (
                        <div key={idx} className="col mb-2">
                            <PatientIcon patient={patient} patientList={bay.patients} getActiveEvent={getActiveEvent}/>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}