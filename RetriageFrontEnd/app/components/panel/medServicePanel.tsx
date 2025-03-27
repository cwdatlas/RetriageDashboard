'use client'

import PatientIcon from "@/app/components/panel/patientIcon";
import {PatientPool} from "@/app/models/patientPool";

export default function MedServicePanel({service} : {service : PatientPool}) {
    return(
        <div>
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
