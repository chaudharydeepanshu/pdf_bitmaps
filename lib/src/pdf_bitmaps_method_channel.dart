import 'package:flutter/foundation.dart';
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
}

/// Parameters for the [pdfPageCount] method.
class PDFPageCountParams {
  /// Provide uris of pdf file for page count.
  final String? pdfUri;

  /// Provide path of pdf file page count.
  final String? pdfPath;

  /// Create parameters for the [pdfPageCount] method.
  const PDFPageCountParams({this.pdfUri, this.pdfPath})
      : assert(pdfUri != null || pdfPath != null,
            'anyone out of pdfUri or pdfPath is required'),
        assert(pdfUri == null || pdfPath == null,
            'either provide only pdfUri or only pdfPath');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfUri': pdfUri,
      'pdfPath': pdfPath,
    };
  }
}

/// Parameters for the [pdfBitmap] method.
class PDFBitmapParams {
  /// Provide uris of pdf file for bitmap.
  final String? pdfUri;

  /// Provide path of pdf file for bitmap.
  final String? pdfPath;

  /// Provide pdf page index for which you want bitmap.
  final int pageIndex;

  /// Provide pdf page bitmap quality from 1 to 100.
  final int quality;

  /// Create parameters for the [pdfBitmap] method.
  const PDFBitmapParams(
      {this.pdfUri, this.pdfPath, required this.pageIndex, this.quality = 100})
      : assert(quality > 0 || quality <= 100,
            'quality should be between 1 to 100'),
        assert(pdfUri == null || pdfPath == null,
            'either provide only pdfUri or only pdfPath');

  Map<String, dynamic> toJson() {
    return <String, dynamic>{
      'pdfUri': pdfUri,
      'pdfPath': pdfPath,
      'pageIndex': pageIndex,
      'quality': quality,
    };
  }
}
