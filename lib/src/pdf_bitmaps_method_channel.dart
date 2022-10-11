import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'pdf_bitmaps_platform_interface.dart';

/// An implementation of [PdfBitmapsPlatform] that uses method channels.
class MethodChannelPdfBitmaps extends PdfBitmapsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('pdf_bitmaps');

  @override
  Future<int?> pdfPageCount({PDFPageCountParams? params}) async {
    final pageCount = await methodChannel.invokeMethod<int?>(
        'pdfPageCount', params?.toJson());
    return pageCount;
  }

  @override
  Future<Uint8List?> pdfBitmap({PDFBitmapParams? params}) async {
    final Uint8List? bytes = await methodChannel.invokeMethod<Uint8List?>(
        'pdfBitmap', params?.toJson());
    return bytes;
  }

  @override
  Future<List<Uint8List>?> pdfBitmaps({PDFBitmapsParams? params}) async {
    final List? bytesList =
        await methodChannel.invokeMethod<List?>('pdfBitmaps', params?.toJson());
    return bytesList?.cast<Uint8List>();
  }

  @override
  Future<String?> cancelBitmaps() async {
    final String? result =
        await methodChannel.invokeMethod<String?>('cancelBitmaps');
    return result;
  }
}

/// Parameters for the [pdfPageCount] method.
class PDFPageCountParams {
  /// Provide path of pdf file page count.
  final String pdfPath;

  /// Create parameters for the [pdfPageCount] method.
  const PDFPageCountParams({required this.pdfPath});

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
    };
  }
}

/// Parameters for the [pdfBitmap] method.
class PDFBitmapParams {
  /// Provide path of pdf file for bitmap.
  final String pdfPath;

  /// Provide pdf page index for which you want bitmap.
  final int pageIndex;

  /// Provide pdf page bitmap scaling from 0.1 to 5.
  final double scale;

  /// Provide pdf page bitmap background color.
  final Color backgroundColor;

  /// Create parameters for the [pdfBitmap] method.
  const PDFBitmapParams(
      {required this.pdfPath,
      required this.pageIndex,
      this.scale = 1,
      this.backgroundColor = Colors.white})
      : assert(scale > 0 || scale <= 5,
            'scale should be greater than 0 and less tan or equal to 5');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'pageIndex': pageIndex,
      'scale': scale,
      'backgroundColor': '#${backgroundColor.value.toRadixString(16)}',
    };
  }
}

/// Parameters for the [pdfBitmaps] method.
class PDFBitmapsParams {
  /// Provide path of pdf file for bitmap.
  final String pdfPath;

  /// Provide pdf page index for which you want bitmap.
  final List<int> pagesIndexes;

  /// Provide pdf page bitmap scaling from 0.1 to 5.
  final double scale;

  /// Provide pdf page bitmap background color.
  final Color backgroundColor;

  /// Create parameters for the [pdfBitmaps] method.
  const PDFBitmapsParams(
      {required this.pdfPath,
      required this.pagesIndexes,
      this.scale = 1,
      this.backgroundColor = Colors.white})
      : assert(pagesIndexes.length > 0, 'pagesIndexes can\'t be empty'),
        assert(scale > 0 || scale <= 5,
            'scale should be greater than 0 and less tan or equal to 5');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfPath': pdfPath,
      'pagesIndexes': pagesIndexes,
      'scale': scale,
      'backgroundColor': '#${backgroundColor.value.toRadixString(16)}',
    };
  }
}
