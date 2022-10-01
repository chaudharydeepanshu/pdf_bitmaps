import 'dart:developer';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:pdf_bitmaps/pdf_bitmaps.dart';
import 'package:pick_or_save/pick_or_save.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _pickOrSavePlugin = PickOrSave();

  final bool _localOnly = false;
  final bool _copyFileToCacheDir = false;
  List<String>? _pickedFilePath;
  Uint8List? pageBytes;

  int pdfPageCount = 0;
  final _pdfBitmapsPlugin = PdfBitmaps();

  @override
  void initState() {
    super.initState();
  }

  Future<void> _filePicker(FilePickerParams params) async {
    List<String>? result;
    try {
      result = await _pickOrSavePlugin.filePicker(params: params);
      log(result.toString());
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;
    setState(() {
      _pickedFilePath = result;
    });
  }

  Future<void> getPDFPageCount({required PDFPageCountParams? params}) async {
    int pageCount;
    try {
      pageCount = await _pdfBitmapsPlugin.pdfPageCount(params: params) ?? 0;
    } on PlatformException {
      pageCount = 0;
    }
    if (!mounted) return;

    setState(() {
      pdfPageCount = pageCount;
    });
  }

  Future<void> getPDFPageBitmap({required PDFBitmapParams? params}) async {
    Uint8List? bytes;
    try {
      bytes = await _pdfBitmapsPlugin.pdfBitmap(params: params);
    } on PlatformException catch (e) {
      log(e.toString());
    }
    if (!mounted) return;

    setState(() {
      pageBytes = bytes;
    });
  }

  bool rejectByteUpdate = false;

  List<Map<String, dynamic>>? listOfBytesAndIndex;

  void updateBytesIntoList(int index) async {
    if (listOfBytesAndIndex![index]["bytes"] == null &&
        rejectByteUpdate != true) {
      rejectByteUpdate = true;
      Uint8List? bytes = await _pdfBitmapsPlugin.pdfBitmap(
          params: PDFBitmapParams(
              pdfUri: _pickedFilePath![0], pageIndex: index, quality: 3));
      setState(() {
        listOfBytesAndIndex![index]["bytes"] = bytes;
        rejectByteUpdate = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            OutlinedButton(
                onPressed: () async {
                  setState(() {
                    listOfBytesAndIndex = null;
                    pdfPageCount = 0;
                    rejectByteUpdate = false;
                  });
                },
                child: const Text('Reset')),
            OutlinedButton(
                onPressed: () async {
                  final params = FilePickerParams(
                    localOnly: _localOnly,
                    copyFileToCacheDir: _copyFileToCacheDir,
                  );
                  await _filePicker(params);
                },
                child: const Text('pick pdf')),
            OutlinedButton(
                onPressed: () {
                  final params = PDFPageCountParams(
                    pdfUri: _pickedFilePath![0],
                  );
                  getPDFPageCount(params: params);
                },
                child: const Text('Get page count of selected pdf')),
            OutlinedButton(
                onPressed: () {
                  listOfBytesAndIndex = List<Map<String, dynamic>>.generate(
                      pdfPageCount,
                      (int index) => {"index": index, "bytes": null},
                      growable: true);
                  setState(() {});
                },
                child: const Text('Generate bytes list for pdf')),
            // OutlinedButton(
            //     onPressed: () {
            //       getPDFPageBitmap(
            //           params: PDFBitmapParams(
            //               pdfUri: _pickedFilePath![0], pageIndex: 1236));
            //     },
            //     child: const Text('Get bitmap')),
            Center(
              child: Text('pdfPageCount: $pdfPageCount'),
            ),
            listOfBytesAndIndex != null
                ? Expanded(
                    child: GridView.builder(
                        gridDelegate:
                            const SliverGridDelegateWithFixedCrossAxisCount(
                          crossAxisCount: 3,
                        ),
                        itemCount: pdfPageCount,
                        itemBuilder: (BuildContext context, int index) {
                          updateBytesIntoList(index);
                          return listOfBytesAndIndex![index]["bytes"] != null
                              ? Image.memory(
                                  listOfBytesAndIndex![index]["bytes"]!,
                                  // width: 100,
                                  // height: 100,
                                  fit: BoxFit.contain,
                                )
                              : const CircularProgressIndicator();
                        }),
                  )
                : const SizedBox(),
          ],
        ),
      ),
    );
  }
}
