'use client'

import {PatientPool} from "@/app/models/patientPool";
import PatientIcon from "@/app/components/panel/patientIcon";
import {useDroppable} from "@dnd-kit/core";

export default function BayPanel({bay}: { bay: PatientPool }) {
    const {isOver, setNodeRef} = useDroppable({
        id: bay.id || 0,
    });
    const style = {
        color: isOver ? 'green' : undefined,
    };
    return (
        <div ref={setNodeRef} style={style}>
            <h2>{bay.name}</h2>
            <div>
                {bay.patients.map((template, idx) => {
                    return (
                        <div key={idx}>
                            <PatientIcon patient={template}/>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}