'use client'

import {PatientPool} from "@/app/models/patientPool";
import PatientIcon from "@/app/components/panel/patientIcon";

export default function BayPanel({bay} : {bay : PatientPool}) {
    return(
        <div>
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