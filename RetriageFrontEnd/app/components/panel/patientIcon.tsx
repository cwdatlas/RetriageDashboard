import { Patient } from "@/app/models/patient";

export default function PatientIcon({ patient }: { patient: Patient }) {
    return (
        <div
            className="border"
            style={{ width: "100px", textAlign: "center" }}
        >
            {/* Top section with dark background and icon */}
            <div className="bg-dark" style={{ padding: "10px" }}>
                <div
                    className="rounded-circle bg-success d-flex justify-content-center align-items-center"
                    style={{
                        width: "50px",
                        height: "50px",
                        margin: "0 auto"  // centers horizontally
                    }}
                >
                    {/* Smaller circle in the center */}
                    <div
                        className="rounded-circle bg-light"
                        style={{ width: "20px", height: "20px" }}
                    />
                </div>
            </div>

            {/* Bottom section with patient card ID */}
            <div style={{ padding: "5px" }}>
                {/* Display the patient's card ID here */}
                <p className="mb-0">{patient.cardId}</p>
            </div>
        </div>
    );
}
