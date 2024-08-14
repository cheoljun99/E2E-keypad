//SecureKeypad.jsx

import '../style/keypad.css'

export default function SecureKeypad({ keypad, onKeyPressed }) {
    //console.log("Rendering image with data:", keypad); // 이미지 데이터 렌더링 전 확인
    const buttonPositions = [
        { top: '4%', left: '2%' },  // 0번 버튼 위치
        { top: '4%', left: '27%' },  // 1번 버튼 위치
        { top: '4%', left: '52%' },  // 2번 버튼 위치
        { top: '4%', left: '77%' },  // 3번 버튼 위치
        { top: '36%', left: '2%' },  // 4번 버튼 위치
        { top: '36%', left: '27%' },  // 5번 버튼 위치
        { top: '36%', left: '52%' },  // 6번 버튼 위치
        { top: '36%', left: '77%' },  // 7번 버튼 위치
        { top: '69%', left: '2%' },  // 8번 버튼 위치
        { top: '69%', left: '27%' },  // 9번 버튼 위치
        { top: '69%', left: '52%' },  // 10번 버튼 위치
        { top: '69%', left: '77%' },  // 11번 버튼 위치
        // 버튼 위치를 이미지에 맞춰 추가로 조정 가능
    ];
    return (
        <div className="keypad-container">
            <table className="table-style">
                <tbody>
                <tr>
                    <td style={{ position: 'relative' }}>
                        <img src={`data:image/png;base64,${keypad}`} alt="Secure Keypad" />
                        {buttonPositions.map((pos, index) => (
                                <button
                                    key={index}
                                    
                                    style={{
                                        position: 'absolute',
                                        top: pos.top,
                                        left: pos.left,
                                        width: '85px',  // 버튼 크기
                                        height: '85px', // 버튼 크기
                                        opacity: 0,     // 버튼을 보이지 않게 함
                                        borderRadius: '50%',  // 버튼을 원형으로 만듦
                                        zIndex: 1,
                                        border: '2px solid black',  // 테두리 추가
                                        backgroundColor: 'rgba(255, 255, 255, 0.7)'  // 약간 투명한 흰색 배경
                                    }}
                                    onClick={() => onKeyPressed(index)}
                                >
                                    {/* 버튼은 보이지 않지만 클릭 가능 */}
                                </button>
                            ))}
                    </td>
                </tr>
                {/* 나머지 키패드 UI */}
                </tbody>
            </table>
        </div>
    );
}
