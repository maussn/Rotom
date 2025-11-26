import React from "react";

interface InfoBox {
  id: number;
  title: string;
  description: string;
}

interface Props {
  items: InfoBox[];
}

const ScrollContainer: React.FC<Props> = ({ items }) => {
  return (
    <div
      style={{
        height: "300px",     // window height
        overflowY: "auto",   // scrolls vertically
        padding: "1rem",
        border: "1px solid #ccc",
        borderRadius: "8px"
      }}
    >
      {items.map((item) => (
        <div
          key={item.id}
          style={{
            padding: "1rem",
            marginBottom: "1rem",
            borderRadius: "8px"
          }}
        >
          <h3 style={{ margin: 0 }}>{item.title}</h3>
          <p style={{ margin: "0.5rem 0 0" }}>{item.description}</p>
        </div>
      ))}
    </div>
  );
};

export default ScrollContainer;