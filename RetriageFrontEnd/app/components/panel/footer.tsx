export default function Footer() {
    return (
        <footer className="bg-light text-center text-lg-start mt-auto">
            <div className="container p-4">
                <div className="row">
                    <div className="col-lg-12 mb-4 mb-md-0">
                        <h5 className="text-uppercase">Devs</h5>
                        <p>Aidan Scott</p>
                        <p>John Botonakis</p>
                    </div>
                </div>
            </div>
            <div
                className="text-center p-3"
                style={{backgroundColor: "rgba(0, 0, 0, 0.2)"}}
            >
                Made as a class project for the Carroll College Nursing Program
            </div>
        </footer>
    );
}
