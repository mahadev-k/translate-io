# translate-io
A language translation project <br/>

This is a spring shell project offering deepgram speech to text translation <br/>

mvn spring-boot:run <br/>

shell command <br/>

sptt <mins> <seconds> <language(Optional)>- This will start recording audio for n minutes and then the whole audio will transcribed and shown in the notepad : Windows <br/>
stop-sptt <id> - Will stop recording and translation. <br/>
translate-file <file> <language(Optional)> - Ex translate-file recordings/record.wav will translate the file to english and show you the transcription <br/>
