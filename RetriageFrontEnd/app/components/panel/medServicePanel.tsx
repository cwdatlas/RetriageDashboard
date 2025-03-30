'use client'

import PatientIcon from "@/app/components/panel/patientIcon";
import {PatientPool} from "@/app/models/patientPool";
import {useDroppable} from "@dnd-kit/core";

export default function MedServicePanel({service}: { service: PatientPool }) {
    const {isOver, setNodeRef} = useDroppable({
        id: service.id || 0,
    });
    const style = {
        color: isOver ? 'green' : undefined,
    };
    return (
        <div ref={setNodeRef} style={style}>
            <h2>{service.name}</h2>
            <div>
                {service.patients.map((template, idx) => {
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
