package com.translate.main.shell;

import com.translate.main.service.impls.DeepgramService;
import com.translate.main.service.interfaces.TranslateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.core.publisher.Flux;

import javax.validation.constraints.Max;
import java.io.FileNotFoundException;

@ShellComponent
@Slf4j
public class TranslateShell {

    @Autowired
    private TranslateService translateService;

    @Autowired
    private DeepgramService deepgramService;

    @ShellMethod("Apply Flux")
    public void flux() {
        log.info("Starting flux");
        Flux<String> strings = Flux.just("Apple", "Orange");
        strings.subscribe(log::info);
        log.info("Ending flux");
    }

    @ShellMethod("Speech To Text")
    public String sptt(
            @ShellOption @Max(30) Integer timeInMins
    ){
        log.info("Started Recording for Translation");
        Integer threadId = translateService.speechToText(deepgramService, timeInMins);
        return "Thread running to stop use command stop-sptt thread id :: "+ threadId;
    }

    @ShellMethod("Stop Translation")
    public String stopSptt(
            @ShellOption Integer id
    ){
        translateService.stopTranslation(id);
        return "SUCCESS";
    }

    @ShellMethod("Translate file")
    public void translateFile(
            @ShellOption String fileName
    ) throws FileNotFoundException {
        translateService.recordingToText(fileName, deepgramService);
    }
}
