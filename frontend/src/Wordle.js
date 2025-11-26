import React, { useState, useEffect, useCallback } from 'react';
import { api } from './Api.js';
import { showMessage } from './utils.js';
import './Wordle.css';

const ROWS = 6;
const COLS = 5;

function Wordle({ gameData, onBack }) {
  const [board, setBoard] = useState(Array.from({ length: ROWS }, () => Array(COLS).fill('')));
  const [tileFeedback, setTileFeedback] = useState(Array.from({ length: ROWS }, () => Array(COLS).fill('')));
  const [currentRow, setCurrentRow] = useState(0);
  const [currentTile, setCurrentTile] = useState(0);
  const [gameOver, setGameOver] = useState(false);
  const [keyboardState, setKeyboardState] = useState({});
  const [gameId] = useState(gameData.gameId);

  const colorMap = {
    GREEN: 'correct',
    YELLOW: 'present',
    ABSENT: 'absent',
    GREY: 'absent',
    GRAY: 'absent'
  };

  const animateRows = (startRow, endRow, feedbackMatrix, callback) => {
    let totalDelay = 0;

    // scheduleTileFlip is now completely independent of loop variables
    const scheduleTileFlip = (r, c, color, delay) => {
      setTimeout(() => {
        const tile = document.getElementById(`tile-${r}-${c}`);
        if (tile) {
          tile.classList.remove('correct', 'present', 'absent', 'flip');
          tile.classList.add(color);
          tile.classList.add('flip');
        }
      }, delay);
    };

    for (let r = startRow; r < endRow; r++) {
      for (let c = 0; c < feedbackMatrix[r].length; c++) {
        const color = feedbackMatrix[r][c];
        scheduleTileFlip(r, c, color, totalDelay);
        totalDelay += 250;
      }
    }

    if (callback) {
      setTimeout(callback, totalDelay + 50);
    }
  };


  useEffect(() => {
    const initGame = () => {
      const newBoard = Array.from({ length: ROWS }, (_, rIdx) => {
        if (gameData.guesses && gameData.guesses[rIdx]) {
          return gameData.guesses[rIdx].word.toUpperCase().split('');
        }
        return Array(COLS).fill('');
      });
      setBoard(newBoard);

      const rowIndex = gameData.guesses ? gameData.guesses.length : 0;
      setCurrentRow(rowIndex);

      const newTileFeedback = Array.from({ length: ROWS }, () => Array(COLS).fill(''));
      const newKeyboard = {};

      if (gameData.guesses) {
        gameData.guesses.forEach((guess, rIdx) => {
          guess.feedback.forEach((fb, cIdx) => {
            const color = colorMap[fb.toUpperCase()] || 'absent';
            newTileFeedback[rIdx][cIdx] = color;

            const letter = guess.word[cIdx].toUpperCase();
            if (color === 'correct') newKeyboard[letter] = 'correct';
            else if (color === 'present' && newKeyboard[letter] !== 'correct') newKeyboard[letter] = 'present';
            else if (color === 'absent' && !newKeyboard[letter]) newKeyboard[letter] = 'absent';
          });
        });
      }

      setKeyboardState(newKeyboard);

      if (gameData.status === 'WON' || gameData.status === 'LOST') setGameOver(true);

      if (rowIndex > 0) {
        setTileFeedback(Array.from({ length: ROWS }, () => Array(COLS).fill('')));
        setTimeout(() => {
          animateRows(0, rowIndex, newTileFeedback, () => {
            setTileFeedback(newTileFeedback);
          });
        }, 50);
      } else {
        setTileFeedback(newTileFeedback);
      }
    };

    initGame();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const handleKeyPress = useCallback((e) => {
    if (gameOver) return;
    const key = e.key.toUpperCase();

    if (key === 'BACKSPACE') {
      if (currentTile > 0) {
        setBoard(prev => {
          const newBoard = prev.map(row => [...row]);
          newBoard[currentRow][currentTile - 1] = '';
          return newBoard;
        });
        setCurrentTile(prev => prev - 1);
      }
    } else if (key === 'ENTER') {
      submitGuess();
    } else if (/^[A-Z]$/.test(key)) {
      if (currentTile < COLS) {
        setBoard(prev => {
          const newBoard = prev.map(row => [...row]);
          newBoard[currentRow][currentTile] = key;
          return newBoard;
        });
        setCurrentTile(prev => prev + 1);
      }
    }
  }, [currentRow, currentTile, gameOver]); // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => {
    document.addEventListener('keydown', handleKeyPress);
    return () => document.removeEventListener('keydown', handleKeyPress);
  }, [handleKeyPress]);

  const submitGuess = async () => {
    if (currentTile < COLS) {
      showMessage('Not enough letters', 'error');
      return;
    }

    const guess = board[currentRow].join('').toLowerCase();

    try {
      const data = await api.submitGuess(guess, gameId);

      const updatedTileFeedback = tileFeedback.map(row => [...row]);
      data.feedback.forEach((fb, idx) => {
        updatedTileFeedback[currentRow][idx] = colorMap[fb.toUpperCase()] || 'absent';
      });
      setTileFeedback(updatedTileFeedback);

      animateRows(currentRow, currentRow + 1, updatedTileFeedback);

      const newKeyboard = { ...keyboardState };
      board[currentRow].forEach((letter, idx) => {
        const color = colorMap[data.feedback[idx].toUpperCase()] || 'absent';
        if (color === 'correct') newKeyboard[letter] = 'correct';
        else if (color === 'present' && newKeyboard[letter] !== 'correct') newKeyboard[letter] = 'present';
        else if (color === 'absent' && !newKeyboard[letter]) newKeyboard[letter] = 'absent';
      });
      setKeyboardState(newKeyboard);

      setTimeout(() => {
        if (data.status === 'WON') {
          setGameOver(true);
          showMessage('Congratulations! You won!', 'success');
        } else if (data.status === 'LOST') {
          setGameOver(true);
          showMessage('Game Over! Better luck next time.', 'error');
        } else {
          setCurrentRow(prev => prev + 1);
          setCurrentTile(0);
        }
      }, COLS * 250 + 50);

    } catch (err) {
      console.error(err);
      showMessage(err.message || 'Failed to submit guess', 'error');
    }
  };

  const renderBoard = () => {
    return board.map((row, rIdx) => (
      <div className="row" key={rIdx}>
        {row.map((letter, cIdx) => {
          const colorClass = tileFeedback[rIdx][cIdx] || '';
          const filledClass = letter ? 'filled' : '';
          return (
            <div
              key={cIdx}
              id={`tile-${rIdx}-${cIdx}`}
              className={`tile ${filledClass} ${colorClass}`}
            >
              {letter}
            </div>
          );
        })}
      </div>
    ));
  };

  return (
    <div className="container">
      <div className="game-screen">
        <button className="back-button" onClick={onBack}>← Back</button>
        <div className="header">
          <h1>WORDLE</h1>
        </div>
        <p className="subtitle">Try to guess today's word</p>
        <div className="game-board">{renderBoard()}</div>
        <div className="keyboard" id="keyboard"></div>
      </div>
    </div>
  );
}

export default Wordle;
