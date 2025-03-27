"use client";

import React, {useState} from 'react';
import {SetCookies} from "@/app/api/cookieApi"

export default function NurseJobModal() {
    const [job, setJob] = useState('');

    function updateCookies() {
        // Save the nurse job as a cookie
        SetCookies("job", job);
    }

    return (
        <div style={overlayStyle}>
            <div style={modalStyle}>
                <form onSubmit={updateCookies}>
                    <label style={{marginBottom: '10px', display: 'block'}}>
                        Enter nurse job:
                        <input
                            type="text"
                            value={job}
                            onChange={(e) => setJob(e.target.value)}
                            style={{marginLeft: '10px'}}
                        />
                    </label>
                    <button type="submit">Submit</button>
                </form>
            </div>
        </div>
    );
};

const overlayStyle: React.CSSProperties = {
    position: 'fixed',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
};

const modalStyle: React.CSSProperties = {
    backgroundColor: '#fff',
    padding: '20px',
    borderRadius: '5px',
};
