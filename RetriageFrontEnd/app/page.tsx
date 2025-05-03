'use client'

import React from 'react'
import Header from '@/app/components/panel/header'
import Footer from '@/app/components/panel/footer'

export default function Home() {
    const images = [
        'mock-mass-casualty.jpg',
        '1.jpg',
        '2.jpg',
        '3.jpg',
        '4.jpg'
    ]

    return (
        <main className="d-flex flex-column min-vh-100">
            <Header />

            {/* Full-width, auto-sliding carousel */}
            <div className="container-fluid p-0">
                <div
                    id="myCarousel"
                    className="carousel slide carousel-fade"
                    data-bs-ride="carousel"
                    data-bs-interval="3000"
                >
                    <div className="carousel-indicators">
                        {images.map((_, idx) => (
                            <button
                                key={idx}
                                type="button"
                                data-bs-target="#myCarousel"
                                data-bs-slide-to={idx}
                                className={idx === 0 ? 'active' : ''}
                                aria-current={idx === 0 ? 'true' : undefined}
                                aria-label={`Slide ${idx + 1}`}
                            />
                        ))}
                    </div>

                    <div className="carousel-inner">
                        {images.map((image, idx) => (
                            <div
                                key={idx}
                                className={`carousel-item${idx === 0 ? ' active' : ''}`}
                            >
                                <img
                                    src={`/images/homepage/${image}`}
                                    className="d-block w-100"
                                    style={{ height: '100vh', objectFit: 'cover' }}
                                    alt={`Slide ${idx + 1}`}
                                />
                            </div>
                        ))}
                    </div>

                    <button
                        className="carousel-control-prev"
                        type="button"
                        data-bs-target="#myCarousel"
                        data-bs-slide="prev"
                    >
                        <span className="carousel-control-prev-icon" aria-hidden="true" />
                        <span className="visually-hidden">Previous</span>
                    </button>
                    <button
                        className="carousel-control-next"
                        type="button"
                        data-bs-target="#myCarousel"
                        data-bs-slide="next"
                    >
                        <span className="carousel-control-next-icon" aria-hidden="true" />
                        <span className="visually-hidden">Next</span>
                    </button>
                </div>
            </div>

            {/* Event details */}
            <div className="container my-4">
                <div className="card">
                    <div className="card-body">
                        <h2 className="card-title">Carroll College Mock Mass Casualty Event</h2>
                        <p className="card-text">
                            The Mass Mock Casualty Event is put on by Kathrin Pieper at the
                            Carroll College nursing department. This website is designed to
                            manage event data and help nursing students gain realistic,
                            fast-paced training that mimics real clinical practice under
                            pressure.
                        </p>
                        <p>
                            This simulation will be run three times throughout the day. The
                            makeup and props will be done by Julie Harris’s theatre class and
                            the nursing lab facilitators as well as volunteers. There will be
                            another station discussing Incident Command Systems and Hospital
                            Incident Command Systems, then a third station that goes over Trauma
                            Informed Care which will be led by Molly Molloy from the Masters of
                            Social Work department and a debrief of the simulation. Our new Nursing
                            Mobile Health Unit and an emergency helicopter will also be on hand.
                        </p>
                    </div>
                </div>
            </div>

            <Footer />
        </main>
    )
}
