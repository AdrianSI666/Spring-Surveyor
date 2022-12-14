import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import './Answers.css';
import { Form, Container, Accordion } from 'react-bootstrap';

const AnswersProfile = () => {
  const localhost = "localhost";
  const port = "8090"

  const { participantId } = useParams();

  const [answerData, setAnswerData] = useState([]);
  const [unexpectedError, setUnexpectedError] = useState(null);
  const fetchAnswerss = () => {
    axios.get(`http://${localhost}:${port}/api/answers/participant/${participantId}`)
      .then(res => {
        setUnexpectedError(null)
        setAnswerData(res.data)
      }).catch(err => {
        console.log(err)
        setUnexpectedError(true)
      })
  }

  useEffect(() => {
    fetchAnswerss();
  }, [])

  const getFilteredArr = (answerData) => {
    const map = new Map();
    answerData.forEach(answer => {
      !map.has(answer.question_id)
        ? map.set(answer.question_id, [answer])
        : map.get(answer.question_id).push(answer)
    });
    return [...map];
  }

  const renderAnswers = getFilteredArr(answerData).map(([key, values]) => {
    return (
      <Accordion key={key} defaultActiveKey={['0']}>
        <Accordion.Item>
          <Accordion.Header>{values[0].question_name}</Accordion.Header>
          <Accordion.Body>
            <Form.Control as='textarea' value={values[0].question_context} disabled />
              {values.map((data) => {
                const id2 = data.participant_id
                const id = data.question_id
                const name = data.participant_nickname
                const content = data.content
                return (
                  <div key={id.toString() + "." + id2.toString()}>
                    <h4>{name}</h4>
                    <Form.Control as='textarea' value={content} disabled />
                  </div>
                )
              })}
          </Accordion.Body>
        </Accordion.Item>
      </Accordion>
    )
  })

  if (answerData !== undefined) {
    return (
      <Container>
        {unexpectedError && <span>Unexpected error occurred. Please try again.</span>}
        {renderAnswers}
      </Container>
    )
  }
  return (
    <Container>
      {unexpectedError && <span>Unexpected error occurred. Please try again.</span>}
    </Container>
  )
}

function Answers() {
  return (
    <div className='answers'>
      <AnswersProfile />
    </div>
  )
}

export default Answers;
