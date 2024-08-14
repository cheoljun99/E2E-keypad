//KeypadUserInput.jsx

import '../style/keypad.css'

export default function KeypadUserInput({ userInput }) {
    return (
        <div className="user-input-container">
            {Array.from({ length: 6 }).map((_, index) => (
                <div
                    key={index}
                    className={`circle ${userInput.length > index ? 'filled' : ''}`}
                ></div>
            ))}
        </div>
    );
}
