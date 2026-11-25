import { useEffect, useState } from "react";

export default function RotatingWord({ words, interval = 2200 }) {
  const [index, setIndex] = useState(0);

  useEffect(() => {
    const id = setInterval(() => {
      setIndex((i) => (i + 1) % words.length);
    }, interval);
    return () => clearInterval(id);
  }, [words, interval]);

  return (
    <span className="inline-grid align-baseline" aria-live="polite">
      {words.map((word, wordIndex) => (
        <span
          key={word}
          className={`col-start-1 row-start-1 whitespace-nowrap ${wordIndex === index ? "animate-word-fade" : "invisible"}`}
        >
          {word}
        </span>
      ))}
    </span>
  );
}