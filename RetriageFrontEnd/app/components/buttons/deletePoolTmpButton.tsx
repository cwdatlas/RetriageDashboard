'use client'

import {useState} from "react";
import {deletePoolTemplate} from "@/app/api/patientPoolTmpApi";

export default function DeletePoolTmpButton(id : number){
    const [error, setError] = useState<string | null>(null);

    function deleteHandler() {
        deletePoolTemplate(id, setError);
        setError("button pressed");
    }

    return (
        <main>
            {error && (<div className="alert alert-danger" role="alert">
                    {error}
                </div>
            )}
            <button type="button" className={"btn btn-primary"} onClick={deleteHandler}>Delete</button>
        </main>
    )
}